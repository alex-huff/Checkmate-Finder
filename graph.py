from PIL import Image, ImageDraw, ImageFont
import json
import sys

with open('moveTree.json') as moveTreeJson:
    moveTree = json.load(moveTreeJson)
    print(moveTree)

print(moveTree['moves'])

nodesPerDepth = [1]
graph = []


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
        # if depth is 0 and not moveTreeNode['isForcedCheckmateTree'][i]: continue
        # if not moveTreeNode['isForcedCheckmateTree'][i]: continue
        incrementNodeCountAtDepth(depth + 1, 1)
        if not nextNodes[i]['escaped']:
            span[1].append((getNodeCountAtDepth(
                depth + 1), moveTreeNode['isForcedCheckmateTree'][i], moveTreeNode['moves'][i]))
        else:
            span[0] = True
        recurseMoveTree(nextNodes[i], depth + 1)
    addNodeToGraph(depth, span)


recurseMoveTree(moveTree, 0)
print(nodesPerDepth)
print(graph)
print(len(graph))

maxNodesPerDepth = max([len(i) for i in graph])
minVerticalSpaceBetweenNodes = 75
columnWidth = 500
margin = 50
graphHeight = (maxNodesPerDepth - 1) * minVerticalSpaceBetweenNodes
graphWidth = (len(graph) - 1) * columnWidth
totalHeight = graphHeight + margin * 2
totalWidth = graphWidth + margin * 2
nodeRadius = 5
graphImage = Image.new("RGB", (totalWidth, totalHeight), (88, 91, 112))
graphDraw = ImageDraw.Draw(graphImage)
forceMateColor = (200, 10, 10)
checkColor = (10, 150, 50)
escapedColor = (203, 166, 247)
nodeColor = (100, 140, 220)
textBackgroundColor = (128, 128, 128)
textBackgroundBorderColor = (0, 0, 0)
maxTextHeight = 50
lineWidth = 4
halfLineWidth = lineWidth / 2
fontName = sys.argv[1]
font = ImageFont.truetype(fontName, 1)


def adjustFontForHeight(text, height):
    global font
    fontSize = 1
    font = ImageFont.truetype(fontName, fontSize)
    while True:
        _, top, _, bottom = font.getbbox(text)
        textHeight = bottom - top
        if textHeight > height:
            break
        fontSize += 1
        font = ImageFont.truetype(fontName, fontSize)

    fontSize -= 1  # rather smaller than bigger
    font = ImageFont.truetype(fontName, fontSize)


def getCoordsAtDepthBredth(depth, bredth):
    numNodesAtDepth = len(graph[depth])
    rowHeight = graphHeight / \
        (numNodesAtDepth - 1) if numNodesAtDepth > 1 else 0
    nodeX = margin + columnWidth * depth
    nodeY = margin + rowHeight * bredth if rowHeight > 0 else margin + graphHeight / 2
    return (nodeX, nodeY)


def lerp(start, end, percent):
    return start + (end - start) * percent


for depth, column in enumerate(graph):
    lineColor = (245, 224, 220) if depth % 2 == 0 else (30, 30, 46)
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
                    # drawl checkmate
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
        continue
    for bredth, node in enumerate(column):
        nodeX, nodeY = getCoordsAtDepthBredth(depth, bredth)
        numNodesAtNextDepth = len(graph[depth + 1])
        nextRowHeight = graphHeight / \
            (numNodesAtNextDepth - 1) if numNodesAtNextDepth > 1 else 0
        targetTextHeight = nextRowHeight / 2 * .7 if nextRowHeight != 0 else maxTextHeight
        targetTextHeight = min(targetTextHeight, maxTextHeight)
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
            textMargin = textHeight / 10
            graphDraw.rounded_rectangle(((textX - textWidth / 2 - textMargin, textY - textHeight / 2 - textMargin), (textX + textWidth / 2 + textMargin,
                                        textY + textHeight / 2 + textMargin)), radius=textHeight / 10, outline=textBackgroundBorderColor, fill=textBackgroundColor, width=lineWidth)
            graphDraw.text((textX - textWidth / 2 - left, textY -
                           textHeight / 2 - top), text=text, font=font)
        graphDraw.arc(((nodeX - nodeRadius, nodeY - nodeRadius), (nodeX +
                      nodeRadius, nodeY + nodeRadius)), 0, 360, nodeColor, nodeRadius + 1)

graphImage.show()
graphImage.save('graph.png')
