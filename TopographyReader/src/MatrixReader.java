/**
 * Class for generating a network diagram from a distance matrix
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.jgraph.JGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.CSVFormat;
import org.jgrapht.ext.CSVImporter;
import org.jgrapht.ext.ImportException;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.jgrapht.graph.ListenableUndirectedGraph;
//import org.jgrapht.graph.SimpleWeightedGraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author reese
 * @param <E>
 *
 */
public class MatrixReader<E> {
	private int width = 810;
	private int height = 610;
	//private static SimpleWeightedGraph<String,DefaultWeightedEdge> g;
	private ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge> g; 
	private int V;
	private int E;
	private double[][] adjMatrix;
	private DefaultWeightedEdge[][] edges;
	private String[] vertices;
	private DefaultWeightedEdge[] edgeList;
	private int current_edge_weight;
	private JGraph jGraph;
	private static final char DEFAULT_DELIMITER = ' ';
	private int rows;
	private int cols;
	//private String ipPrefix = "192.168.0.";
	private String ipPrefix = "n";
	Object[] vertexList;
	
	public <E> MatrixReader(String fileName) throws IOException {
		super();
		//System.out.println("Creating MatrixReader");
		try {			
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			rows = countRows(fileName);
			cols = countCols(fileName);
			adjMatrix = new double[rows][cols];
			Scanner scanner = new Scanner (input);
			while(scanner.hasNextDouble()) {
				for(int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						adjMatrix[i][j] = scanner.nextDouble();
					}
				}
			}
			
			scanner.close();			
		
		} catch (IOException e)
	    {
	        System.out.println("Error reading");
	    }		
	}
	
	//public SimpleWeightedGraph<String, DefaultWeightedEdge> createGraph() {
	public ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge> createGraph() {
		//Graph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		//Graph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		//g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		g = new ListenableUndirectedWeightedGraph <String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		String ipHost;
		edges = new DefaultWeightedEdge[rows][cols];
        vertices = new String[rows];
        
        //Add vertices from file
        //System.out.printf("\nVertices");
        for(int i = 0; i < vertices.length; i++) {
        	ipHost = Integer.toString(i);
        	vertices[i] = ipPrefix.concat(ipHost);
        	g.addVertex(vertices[i]);
        	//System.out.printf("\n%s", vertices[i].toString());
        }
              
        //Add edges from file
	    for (int i = 0; i < rows; i++) {
	        for (int j = 0; j < cols; j++) { 
	        	if((i != j) && adjMatrix[i][j] > 0) {
	        		//Null values entered on duplicate edges
	        		edges[i][j] = g.addEdge(vertices[i], vertices[j]);
	        		//System.out.println(i + "," + j + ": " + edges[i][j]);
	        	}
	        }
	    }
	    
	    //Add weights to graph
        for(int i = 0; i < rows; i++) {
    		for(int j = 0; j < cols; j++) {
    			if(edges[i][j] != null) {
    				g.setEdgeWeight(edges[i][j], adjMatrix[i][j]);
    				//System.out.println(edges[i][j].toString() + "\t" + adjMatrix[i][j]);
    			}    			   				
        	}
        }        
		
        vertexList = g.vertexSet().toArray();
		Map<String, String> map = new HashMap<String, String>();
        //System.out.println("graph=" + g);
        return g;
	}
	
	
	/**
	 * Count the number of rows in the matrix file
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public int countRows(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        //System.out.printf("\nRows: %d", count);
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	public int countCols(String fileName) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(fileName));
		String [] line = input.readLine().trim().split("\\s+");
		//System.out.printf("\nColumns: %d", line.length);
		return line.length;
	}
	
	public void flattenArray(DefaultWeightedEdge[][] arr) {
	    List<DefaultWeightedEdge> list = new ArrayList<DefaultWeightedEdge>();
	    for (int i = 0; i < rows; i++) {
	        for (int j = 0; j < cols; j++) { 
	            list.add(arr[i][j]); 
	        }
	    }

	    edgeList = (DefaultWeightedEdge[]) new DefaultWeightedEdge[list.size()];
	    for (int i = 0; i < edgeList.length; i++) {
	    	edgeList[i] = list.get(i);
	    }
	}
	
	/**
	 * @param g
	 * @param format
	 * @param delimiter
	 * @return
	 */
	 public <E> CSVImporter<String, E> createImporter(
		        Graph<String, E> g, CSVFormat format, Character delimiter){
        return new CSVImporter<>(
            (l, a) -> l, (f, t, l, a) -> g.getEdgeFactory().createEdge(f, t), format, delimiter);
	}
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the g
	 */
	//public SimpleWeightedGraph<String, DefaultWeightedEdge> getG() {
	public ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge> getG() {
		return g;
	}

	/**
	 * @return the v
	 */
	public int getV() {
		return V;
	}

	/**
	 * @return the e
	 */
	public int getE() {
		return E;
	}

	/**
	 * @return the adjMatrix
	 */
	public double[][] getAdjMatrix() {
		return adjMatrix;
	}

	/**
	 * @return the current_edge_weight
	 */
	public int getCurrent_edge_weight() {
		return current_edge_weight;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @param g the g to set
	 */
	//public void setG(SimpleWeightedGraph<String, DefaultWeightedEdge> g) {
	public void setG(ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge> g) {
		this.g = g;
	}

	/**
	 * @param v the v to set
	 */
	public void setV(int v) {
		V = v;
	}

	/**
	 * @param e the e to set
	 */
	public void setE(int e) {
		E = e;
	}
	
	public String getVertex(int vertexNum) {
		return vertices[vertexNum];
	}

	/**
	 * @param adjMatrix the adjMatrix to set
	 */
	public void setAdjMatrix(double[][] adjMatrix) {
		this.adjMatrix = adjMatrix;
	}

	/**
	 * @param current_edge_weight the current_edge_weight to set
	 */
	public void setCurrent_edge_weight(int current_edge_weight) {
		this.current_edge_weight = current_edge_weight;
	}
	
	public void printAdjMatrix() {
		//Print the distance matrix
		System.out.printf("\nMatrix\n");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.printf("%d\t",adjMatrix[i][j]);
			}
			System.out.printf("\n");
		}        
	}
	
	public void dijkstraTable(String vertex) {
		GraphPath<String, DefaultWeightedEdge> dijkPath;
        List<DefaultWeightedEdge> pathWalk;
        SingleSourcePaths<String, DefaultWeightedEdge> pathsTree = 
        		new DijkstraShortestPath<String, DefaultWeightedEdge>(g).getPaths(vertex);
        generateFlowTable(pathsTree, vertex);
	}
	
	public void bellmanFord(String vertex) {
		System.out.printf("\n\nBellman-Ford Shortest Path");
        //BellmanFordShortestPath bfsp = new BellmanFordShortestPath(graph);
        SingleSourcePaths<String, DefaultWeightedEdge> bfpPathsTree = 
        		new BellmanFordShortestPath<String, DefaultWeightedEdge>(g).getPaths(vertex);
        //GraphPath<String, DefaultWeightedEdge> bfspPath = DijkstraShortestPath.findPathBetween(graph, "192.168.0.1", "192.168.0.8");
        //GraphPath<String, DefaultWeightedEdge> bfspPath;
        //List<DefaultWeightedEdge> bfpPathWalk;        
        //String[] hops;
        generateFlowTable(bfpPathsTree, vertex);
	}
	
	public void generateFlowTable(SingleSourcePaths<String, DefaultWeightedEdge> pathTree, String vertex) {
		GraphPath<String, DefaultWeightedEdge> path;
        List<DefaultWeightedEdge> pathWalk;
        SingleSourcePaths<String, DefaultWeightedEdge> pathsTree = 
        		new BellmanFordShortestPath<String, DefaultWeightedEdge>(g).getPaths(vertex);
        String[] hops;
        
        /*
        System.out.printf("Port\t\tEther Src\tEther Type\tVlan ID\t\tIP Dest\t\tIP Src\t\tTCP Dst\tTCP Src\tIP Proto");
		for(int i = 0; i < vertices.length; i++) {
			path = pathTree.getPath(vertices[i]);
        	if(!path.getStartVertex().equals(path.getEndVertex())) {
        		pathWalk = path.getEdgeList();        		
        		hops = pathWalk.get(0).toString().split(":");
        		System.out.printf("\n%s",hops[1].substring(0, hops[1].length()-1).trim());	//Port
        		System.out.printf("\t*");   //Ether Src
        		System.out.printf("\t\t*"); //Ether Type
        		System.out.printf("\t\t*"); //Vlan ID
        		System.out.printf("\t\t%s",path.getEndVertex()); //IP Dest
        		System.out.printf("\t%s",path.getStartVertex());   //IP Src
        		System.out.printf("\t*");   //TCP Dst
        		System.out.printf("\t*");   //TCP Src
        		System.out.printf("\t*");   //IP Proto
        	}
        }
        */
        
        FlowTable table = new FlowTable();
        table.addRow("Switch Port", "MAC src", "MAC dst", "Ether Src","Ether Type","Vlan ID", "IP Src","IP Dest", "TCP Dst","TCP Src","IP Proto","Action");
        table.addRow("-----------", "-------", "-------", "---------", "---------", "------", "------", "------", "-------", "------", "-------", "-----");
        
        for(int i = 0; i < vertexList.length; i++) {        	
        	path = pathsTree.getPath(vertexList[i].toString());
        	pathWalk = path.getEdgeList();

        	if(!path.getStartVertex().equals(path.getEndVertex())) {
        		//System.out.printf("\n%s",dijkPath.getStartVertex());
        		hops = pathWalk.get(0).toString().split(":");
        		table.addRow("*", 
        				"*", 
        				"*", 
        				"*", 
        				"*", 
        				"*", 
        				path.getStartVertex(), 
        				path.getEndVertex(), 
        				"*", 
        				"*", 
        				"*", hops[1].substring(0, hops[1].length()-1).trim());
        	}
        }
        System.out.println(table.toString());
	}

	/**
	 * @throws InterruptedException 
	 * @param args
	 * @throws ImportException 
	 * @throws IOException 
	 * @throws  
	 */
	public static <E> void main(String[] args) throws IOException, ImportException, InterruptedException {
		// TODO Auto-generated method stub
		//Create from file
		//MatrixReader<E> matrixReader = new MatrixReader<E>("src/files/kn57_distance_matrix.txt");
		MatrixReader<E> matrixReader = new MatrixReader<E>("./files/mst2_distance_matrix.txt");
		JGraphLayout layout = new JGraphHierarchicalLayout();
		
		matrixReader.createGraph();	
		matrixReader.dijkstraTable(matrixReader.getVertex(0));
		//matrixReader.bellmanFord(matrixReader.getVertex(0));
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(matrixReader.getWidth(), matrixReader.getHeight());
		frame.setBounds(10, 10, matrixReader.getWidth() * 2, matrixReader.getHeight() * 2);
		JGraph jgraph = new JGraph(new JGraphModelAdapter(matrixReader.getG()));
		JGraphFacade facade = new JGraphFacade(jgraph);
		layout.run(facade);
		Map nested = facade.createNestedMap(false, false);
		jgraph.getGraphLayoutCache().edit(nested);
		frame.getContentPane().add(jgraph);
		frame.setVisible(true);
		while (true) {
			Thread.sleep(2000);
		}
	}
}
