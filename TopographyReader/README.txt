Updated by Tyler Yox on 1/27/2018

Navigate to the TopographyReader directory.



To compile: javac -cp .\lib\*;.\bin -d .\bin -s .\src .\src\TopographyReader.java

***IF YOU HAVE ISSUES COMPILING, DELETE ALL LIBRARIES in \lib\ PRECEDED WITH '._'***

To run: javaw -cp .\bin\;.\lib\* TopographyReader [filename] [path algorithm] [layout]

  filename: file of type '.graphml'. Can be found in the files directory.

  path algorithm: Switch for handling shortest path algorithm.
    -alg:0 - Dijkstra shortest path algorithm
    -alg:1 - Bellman-Ford shortest path algorithm

  layout: Switch for handling visualization of the network graph.
    -layout:0 - Hierarchical layout
    -layout:1 - Circle layout


    EXAMPLE: javaw -cp .\bin\;.\lib\* TopographyReader .\files\Internet2.graphml alg:0 layout:1
