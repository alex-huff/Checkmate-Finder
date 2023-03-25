from io import BufferedWriter
from PIL import Image, ImageDraw, ImageFont
import argparse
import json
import sys
import re

parser = argparse.ArgumentParser(
    description='ChessGrapher: Python program that graphs the move tree outputted from CheckmateFinder.',
    epilog='ChessGrapher needs a move tree. Use CheckmateFinder to graph a move tree before using ChessGrapher.'
)
parser.add_argument('moveTree', type=argparse.FileType(
    'r'), help='a JSON file containing the move tree')
parser.add_argument('font', type=argparse.FileType(
    'rb'), help='a TrueType or OpenType font file to use when graphing')
parser.add_argument('--outputFile', dest='outputFile',
                    help='output file', type=argparse.FileType('wb'))
parser.add_argument('--width', dest='width', default=1920,
                    type=int, help='width of the output image')
parser.add_argument('--height', dest='height', default=1080,
                    type=int, help='height of the output image')
parser.add_argument('--margin', dest='margin', default=50,
                    type=int, help='margin of the output image')
parser.add_argument('--maxTextBoxHeight', dest='maxTextBoxHeight',
                    default=50, type=int, help='max height for text labels')
parser.add_argument('--lineWidth', dest='lineWidth',
                    default=4, type=int, help='width of graph lines')
parser.add_argument('--nodeRadius', dest='nodeRadius',
                    default=5, type=int, help='radius of graph nodes')
parser.add_argument('--textBoxRadiusScale', dest='textBoxRadiusScale',
                    default=5, choices=range(0, 11), type=float, help='radius scale for text boxes')
parser.add_argument('--textBoxBorderWidthScale', dest='textBoxBorderWidthScale',
                    default=5, choices=range(0, 11), type=float, help='line width scale for text boxes')
parser.add_argument('--skipWrongStartingMoves', dest='skipWrongStartingMoves',
                    action='store_true', help='skip starting moves that don\'t lead to a forced checkmate')
parser.add_argument('--skipWrongMoves', dest='skipWrongMoves', action='store_true',
                    help='skip moves that don\'t lead to a forced checkmate')
parser.add_argument('--showImage', dest='showImage', action='store_true',
                    help='show image in default image viewer after completion')
parser.add_argument('--showImageAtEachDepth', dest='showImageAtEachDepth', action='store_true',
                    help='same as --showImage but for every depth')
parser.add_argument('--backgroundColor', dest='backgroundColor',
                    default='#585B70', help='color for the background')
parser.add_argument('--forceMateColor', dest='forceMateColor',
                    default='#C80A0A', help='color for marking force mate move tree paths')
parser.add_argument('--checkColor', dest='checkColor',
                    default='#0A9632', help='color for marking checkmate game states')
parser.add_argument('--escapedColor', dest='escapedColor',
                    default='#CBA6F7', help='color for marking spots where max depth was reached')
parser.add_argument('--nodeColor', dest='nodeColor',
                    default='#648CDC', help='color for nodes (game states)')
parser.add_argument('--textColor', dest='textColor',
                    default='#000000', help='color for text')
parser.add_argument('--textBackgroundColor', dest='textBackgroundColor',
                    default='#808080', help='color for text background')
parser.add_argument('--textBackgroundBorderColor', dest='textBackgroundBorderColor',
                    default='#000000', help='color for text background border')
parser.add_argument('--whiteColor', dest='whiteColor',
                    default='#F5E0DC', help='color for white')
parser.add_argument('--blackColor', dest='blackColor',
                    default='#1E1E2E', help='color for black')

args = parser.parse_args()

print('Loading move tree...')
moveTree = json.load(args.moveTree)
args.moveTree.close()

nodesPerDepth = [1]
graph = []
hexRegex = re.compile('^#?([A-Fa-f0-9]{6})$')


def hexToRGB(hexString):
    matches = hexRegex.findall(hexString)
    if len(matches) == 0:
        print(f'Invalid hex color: {hexString}', file=sys.stderr)
        sys.exit(-1)
    hexCode = matches[0]
    return tuple(int(hexCode[i:i + 2], 16) for i in (0, 2, 4))


def lerp(start, end, percent):
    return start + (end - start) * percent


def incrementNodeCountAtDepth(depth, count):
    if depth is len(nodesPerDepth):
        nodesPerDepth.append(0)
    nodesPerDepth[depth] = nodesPerDepth[depth] + count


def getNodeCountAtDepth(depth):
    return nodesPerDepth[depth]


def addNodeToGraph(depth, span):
    if depth >= len(graph):
        for _ in range((depth - len(graph)) + 1):
            graph.append([])
    graph[depth].append(span)


def recurseMoveTree(moveTreeNode, depth):
    if 'nextNodes' not in moveTreeNode:
        return
    nextNodes = moveTreeNode['nextNodes']
    span = [False, []]
    for i in range(len(nextNodes)):
        if skipWrongStartingMoves and depth == 0 and not moveTreeNode['isForcedCheckmateTree'][i]:
            continue
        if skipWrongMoves and not moveTreeNode['isForcedCheckmateTree'][i]:
            continue
        incrementNodeCountAtDepth(depth + 1, 1)
        if not nextNodes[i]['escaped']:
            span[1].append((getNodeCountAtDepth(
                depth + 1), moveTreeNode['isForcedCheckmateTree'][i], moveTreeNode['moves'][i]))
        else:
            span[0] = True
        recurseMoveTree(nextNodes[i], depth + 1)
    addNodeToGraph(depth, span)


skipWrongStartingMoves = args.skipWrongStartingMoves
skipWrongMoves = args.skipWrongMoves
print('Generating graph structure...')
recurseMoveTree(moveTree['a'], 0)

maxNodesPerDepth = max([len(i) for i in graph])
startingTeam = 'white' if moveTree['b'] else 'black'
totalWidth = args.width
totalHeight = args.height
margin = args.margin
graphWidth = totalWidth - margin * 2
graphHeight = totalHeight - margin * 2
columnWidth = graphWidth / (len(graph) - 1)
nodeRadius = args.nodeRadius
lineWidth = args.lineWidth
textBoxRadiusScale = args.textBoxRadiusScale / 10
textBoxBorderWidthScale = args.textBoxBorderWidthScale / 10
showImage = args.showImage
showImageAtEachDepth = args.showImageAtEachDepth
outputFile = args.outputFile
if outputFile is None:
    outputFile = 'graph.png'
backgroundColor = hexToRGB(args.backgroundColor)
forceMateColor = hexToRGB(args.forceMateColor)
checkColor = hexToRGB(args.checkColor)
escapedColor = hexToRGB(args.escapedColor)
nodeColor = hexToRGB(args.nodeColor)
textColor = hexToRGB(args.textColor)
textBackgroundColor = hexToRGB(args.textBackgroundColor)
textBackgroundBorderColor = hexToRGB(args.textBackgroundBorderColor)
blackColor = hexToRGB(args.blackColor)
whiteColor = hexToRGB(args.whiteColor)
maxTextBoxHeight = args.maxTextBoxHeight
graphImage = Image.new("RGB", (totalWidth, totalHeight), backgroundColor)
graphDraw = ImageDraw.Draw(graphImage)
fontFile = args.font
font = ImageFont.truetype(fontFile, 1)


def adjustFontForHeight(text, height):
    global font
    fontSize = 1
    font = font.font_variant(size=fontSize)
    while True:
        _, top, _, bottom = font.getbbox(text)
        textHeight = bottom - top
        if textHeight > height:
            break
        fontSize += 1
        font = font.font_variant(size=fontSize)

    fontSize -= 1  # rather smaller than bigger
    font = font.font_variant(size=fontSize)


def getCoordsAtDepthBredth(depth, bredth):
    numNodesAtDepth = len(graph[depth])
    rowHeight = graphHeight / \
        (numNodesAtDepth - 1) if numNodesAtDepth > 1 else 0
    nodeX = margin + columnWidth * depth
    nodeY = margin + rowHeight * bredth if rowHeight > 0 else margin + graphHeight / 2
    return (nodeX, nodeY)


for depth, column in enumerate(graph):
    match startingTeam:
        case 'white': lineColor = whiteColor if depth % 2 == 0 else blackColor
        case 'black': lineColor = whiteColor if depth % 2 == 1 else blackColor
    numNodesAtDepth = len(graph[depth])
    rowHeight = graphHeight / \
        (numNodesAtDepth - 1) if numNodesAtDepth > 1 else 0
    for bredth, node in enumerate(column):
        nodeX, nodeY = getCoordsAtDepthBredth(depth, bredth)
        if node[0]:
            escapedYs = [nodeY - rowHeight / 3.5,
                         nodeY, nodeY + rowHeight / 3.5]
            for y in escapedYs:
                graphDraw.line(((nodeX, nodeY), (totalWidth, y)),
                               escapedColor, lineWidth)
        else:
            for nextBredth in node[1]:
                nextNodeCoords = getCoordsAtDepthBredth(
                    depth + 1, nextBredth[0] - 1)
                isForcematePath = nextBredth[1]
                if isForcematePath:
                    graphDraw.line(((nodeX, nodeY), nextNodeCoords),
                                   forceMateColor, lineWidth * 3)
                graphDraw.line(((nodeX, nodeY), nextNodeCoords),
                               lineColor, lineWidth)
                isConnectingToLeaf = len(
                    graph[depth + 1][nextBredth[0] - 1][1]) == 0
                if isForcematePath and isConnectingToLeaf and depth % 2 == 0:
                    # draw checkmate
                    checkX, checkY = nextNodeCoords
                    checkLines =\
                        [
                            ((checkX - nodeRadius - lineWidth, checkY - 3 * nodeRadius - lineWidth),
                             (checkX - nodeRadius - lineWidth, checkY + 3 * nodeRadius + lineWidth)),
                            ((checkX + nodeRadius + lineWidth, checkY - 3 * nodeRadius - lineWidth),
                             (checkX + nodeRadius + lineWidth, checkY + 3 * nodeRadius + lineWidth)),
                            ((checkX - 3 * nodeRadius - lineWidth, checkY - nodeRadius - lineWidth),
                             (checkX + 3 * nodeRadius + lineWidth, checkY - nodeRadius - lineWidth)),
                            ((checkX - 3 * nodeRadius - lineWidth, checkY + nodeRadius + lineWidth),
                             (checkX + 3 * nodeRadius + lineWidth, checkY + nodeRadius + lineWidth)),
                        ]
                    for line in checkLines:
                        graphDraw.line(line, checkColor, lineWidth)
        graphDraw.arc(((nodeX - nodeRadius, nodeY - nodeRadius), (nodeX +
                      nodeRadius, nodeY + nodeRadius)), 0, 360, nodeColor, nodeRadius + 1)
    # text pass
    if depth is len(graph) - 1:
        break
    for bredth, node in enumerate(column):
        nodeX, nodeY = getCoordsAtDepthBredth(depth, bredth)
        numNodesAtNextDepth = len(graph[depth + 1])
        nextRowHeight = graphHeight / \
            (numNodesAtNextDepth - 1) if numNodesAtNextDepth > 1 else 0
        targetTextBoxHeight = (
            nextRowHeight / 2) * .85 if nextRowHeight != 0 else maxTextBoxHeight
        targetTextBoxHeight = min(targetTextBoxHeight, maxTextBoxHeight)
        textBoxBorderWidth = round(
            (targetTextBoxHeight / 6) * textBoxBorderWidthScale)
        textBoxMargin = textBoxBorderWidth * 2
        textBoxRadius = (targetTextBoxHeight / 2.1) * textBoxRadiusScale
        targetTextHeight = targetTextBoxHeight - textBoxBorderWidth * 4
        for nextBredth in node[1]:
            nextNodeCoords = getCoordsAtDepthBredth(
                depth + 1, nextBredth[0] - 1)
            textX = lerp(nodeX, nextNodeCoords[0], .5)
            textY = lerp(nodeY, nextNodeCoords[1], .5)
            text = nextBredth[2]
            adjustFontForHeight(text, targetTextHeight)
            left, top, right, bottom = font.getbbox(text)
            textWidth = right - left
            textHeight = bottom - top
            graphDraw.rounded_rectangle(((textX - textWidth / 2 - textBoxMargin, textY - textHeight / 2 - textBoxMargin), (textX + textWidth / 2 + textBoxMargin,
                                        textY + textHeight / 2 + textBoxMargin)), radius=textBoxRadius, outline=textBackgroundBorderColor, fill=textBackgroundColor, width=textBoxBorderWidth)
            graphDraw.text((textX - textWidth / 2 - left, textY -
                           textHeight / 2 - top), text=text, font=font, fill=textColor)
        graphDraw.arc(((nodeX - nodeRadius, nodeY - nodeRadius), (nodeX +
                      nodeRadius, nodeY + nodeRadius)), 0, 360, nodeColor, nodeRadius + 1)
    if showImageAtEachDepth:
        graphImage.show()

if showImage or showImageAtEachDepth:
    graphImage.show()
graphImage.save(outputFile)
if type(outputFile) is BufferedWriter:
    outputFile.close()
