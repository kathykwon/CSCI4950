
/*
 * Test class for jgrapht and jgraph
 */

import java.awt.Color;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;


/**
 *
 * @author Reese
 */
public class GraphTest {
    public static void main(String[] args) throws Exception{
    	SimpleWeightedGraph<String,DefaultWeightedEdge> graph = 
    			new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    	int width = 810;
    	int height = 610; 
    	int numVertices = 8;
    	int numEdges = 10;
    	String ipPrefix = "192.168.0.";
    	String ipHost;
        double x, y;
        double seed = System.currentTimeMillis();
        Random rdm = new java.util.Random((long)seed);
        rdm.setSeed((long)seed);
        DefaultWeightedEdge[] edges = new DefaultWeightedEdge[numEdges];
        String[] vertices = new String[numVertices];
        
        //Add vertices
        for(int i = 0; i < numVertices; i++) {
        	ipHost = Integer.toString(i+1);
        	vertices[i] = ipPrefix.concat(ipHost);
        	graph.addVertex(vertices[i]);
        }              
        
        //Add edges
        edges[0] = graph.addEdge("192.168.0.1", "192.168.0.2");        
        edges[1] = graph.addEdge("192.168.0.1", "192.168.0.3");        
        edges[2] = graph.addEdge("192.168.0.1", "192.168.0.4");        
        edges[3] = graph.addEdge("192.168.0.3", "192.168.0.2");        
        edges[4] = graph.addEdge("192.168.0.3", "192.168.0.5");        
        edges[5] = graph.addEdge("192.168.0.4", "192.168.0.5");        
        edges[6] = graph.addEdge("192.168.0.4", "192.168.0.6");        
        edges[7] = graph.addEdge("192.168.0.5", "192.168.0.7");        
        edges[8] = graph.addEdge("192.168.0.6", "192.168.0.7");        
        edges[9] = graph.addEdge("192.168.0.7", "192.168.0.8");
        
        //Add weights
        for(int i = 0; i < numEdges; i++) {
        	graph.setEdgeWeight(edges[i], Math.random()*10);
        }

        //System.out.printf("%s\n", graph.toString());
        
        // Set up vertex and edge display properties for jgraph
        AttributeMap attrVertex = JGraphModelAdapter.createDefaultVertexAttributes();
        AttributeMap attrEdge = JGraphModelAdapter.createDefaultEdgeAttributes(graph);
        GraphConstants.setLabelEnabled(attrEdge, false);
        GraphConstants.setLineColor(attrEdge, Color.BLUE);
        GraphConstants.setLabelAlongEdge(attrEdge, true);
        
        // Use jgraph to create a view of the graph
        @SuppressWarnings("rawtypes")
		JGraphModelAdapter jgAdapter = new JGraphModelAdapter(graph, attrVertex, attrEdge);
        JGraph jGraph = new JGraph(jgAdapter);
        //GraphModel model = jGraph.getModel();
        //GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
        //GraphLayoutCache cache = jGraph.getGraphLayoutCache();
        
        /*
        //Place vertices in random locations
        for(Object item : jGraph.getRoots()) {
        	//System.out.printf("%s\t",item.toString());
        	//Each vertex is a GraphCell instance
        	GraphCell cell = (GraphCell) item;
        	//look up the corresponding CellView
        	CellView view = cache.getMapping(cell,true);
        	//getBounds is a shortcut to the vertex location and size
        	Rectangle2D bounds = view.getBounds();
        	x = rdm.nextDouble() * width;
        	y = rdm.nextDouble() * height;
        	//System.out.printf("(%s,%s)\n", x,y);
        	bounds.setRect(x, y, bounds.getWidth(), bounds.getHeight());
        }
        */
        
        System.out.println("Dijkstra's Shortest Path");
        //DijkstraShortestPath dijk = new DijkstraShortestPath(graph);
        //GraphPath<String, DefaultWeightedEdge> dijkPath = 
        //		DijkstraShortestPath.findPathBetween(graph, "192.168.0.1", "192.168.0.8");        
        
        //Get a list of paths from source to all destinations
        SingleSourcePaths<String, DefaultWeightedEdge> pathsTree = 
        		new DijkstraShortestPath<String, DefaultWeightedEdge>(graph).getPaths("192.168.0.1");       
               
        /*
         * Test a single path
         * 
        GraphPath<String, DefaultWeightedEdge> dijkPath5 = pathsTree.getPath("192.168.0.5");
        List<DefaultWeightedEdge> pathWalk5 = dijkPath5.getEdgeList();
        for(int i = 0; i < pathWalk5.size(); i++)
        	System.out.println(pathWalk5.get(i));
        System.out.println("Weight: " + dijkPath5.getWeight());
        */
        
        GraphPath<String, DefaultWeightedEdge> dijkPath;
        List<DefaultWeightedEdge> pathWalk;
        String[] hops;
        System.out.printf("Port\t\tEther Src\tEther Type\tVlan ID\t\tIP Dest\t\tIP Src\tTCP Dst\tTCP Src\tIP Proto");
        for(int i = 0; i < numVertices; i++) {
        	dijkPath = pathsTree.getPath(vertices[i]);
        	pathWalk = dijkPath.getEdgeList();

        	if(!dijkPath.getStartVertex().equals(dijkPath.getEndVertex())) {
        		//System.out.printf("\n%s",dijkPath.getStartVertex());
        		hops = pathWalk.get(0).toString().split(":");
        		System.out.printf("\n%s",hops[1].substring(0, hops[1].length()-1).trim());	//Port
        		System.out.printf("\t*");   //Ether Src
        		System.out.printf("\t\t*"); //Ether Type
        		System.out.printf("\t\t*"); //Vlan ID
        		System.out.printf("\t\t%s",dijkPath.getEndVertex()); //IP Dest
        		System.out.printf("\t*");   //IP Src
        		System.out.printf("\t*");   //TCP Dst
        		System.out.printf("\t*");   //TCP Src
        		System.out.printf("\t*");   //IP Proto
        	}
        }
        
        /*
         * See all paths from source to destination
         * 
        for(int i = 0; i < numVertices; i++) {
        	dijkPath = pathsTree.getPath(vertices[i]);
        	if(!dijkPath.getStartVertex().equals(dijkPath.getEndVertex())) {
	        	pathWalk = dijkPath.getEdgeList();
	        	System.out.printf("\n");
	        	for(int j = 0; j < pathWalk.size(); j++)
	            	System.out.printf("\t%s",pathWalk.get(j));
	        	//System.out.printf("\tWeight: %f", pathsTree.getWeight(vertices[i]));
        	}
        }
        */
        
        System.out.printf("\n\nBellman-Ford Shortest Path");
        //BellmanFordShortestPath bfsp = new BellmanFordShortestPath(graph);
        SingleSourcePaths<String, DefaultWeightedEdge> bfpPathsTree = 
        		new BellmanFordShortestPath<String, DefaultWeightedEdge>(graph).getPaths("192.168.0.1");
        //GraphPath<String, DefaultWeightedEdge> bfspPath = DijkstraShortestPath.findPathBetween(graph, "192.168.0.1", "192.168.0.8");
        GraphPath<String, DefaultWeightedEdge> bfspPath;
        List<DefaultWeightedEdge> bfpPathWalk;
        for(int i = 0; i < numVertices; i++) {
        	bfspPath = bfpPathsTree.getPath(vertices[i]);
        	if(!bfspPath.getStartVertex().equals(bfspPath.getEndVertex())) {
        		bfpPathWalk = bfspPath.getEdgeList();
	        	System.out.printf("\n");
	        	//System.out.printf("\t%s",bfpPathWalk.get(0));
	        	for(int j = 0; j < bfpPathWalk.size(); j++)
	            	System.out.printf("\t%s",bfpPathWalk.get(j));
	        	System.out.printf("\tWeight: %f", pathsTree.getWeight(vertices[i]));
        	}
        }
        
        JFrame frame = new JFrame();
		frame.setSize(400, 400);		
		frame.setBounds(10, 10, width, height);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JScrollPane(jGraph));
		frame.setVisible(true);
		while (true) {			
			Thread.sleep(2000);
		}
                
    }
    
}
