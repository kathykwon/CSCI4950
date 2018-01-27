import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.ImportException;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import org.jgraph.JGraph;
import com.jgraph.layout.JGraphCompoundLayout;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
//import com.jgraph.layout.graph.JGraphAnnealingLayout;
//import com.jgraph.layout.graph.JGraphISOMLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
//import com.jgraph.layout.graph.JGraphSpringLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphSelfOrganizingOrganicLayout;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;
//import com.jgraph.layout.tree.JGraphMoenLayout;
import com.jgraph.layout.tree.JGraphRadialTreeLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import com.jgraph.layout.tree.OrganizationalChart;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.commons.cli.*;

/**
 * Read a topology map from a .graphml file, convert it into a graph object, calculate
 * the shortest paths, generate an OpenFlow style flow table, and display a visualization
 * of the network graph.
 *
 * @author Reese Troup
 * @param <E>
 *
 */

public class TopographyReader<E> {

	/**
	 * @param <E>
	 * @param args
	 * @throws ImportException
	 * @throws Exception;
	 */
	private String fileName;
 private File graphFile;
	private int width = 810;
	private int height = 610;
	private GraphMLImporter<String, DefaultWeightedEdge> impl;
	//private SimpleWeightedGraph<String,DefaultWeightedEdge> g;
	private Graph<String, DefaultWeightedEdge> g;
	Object[] vertexList;
	private static JGraphLayout layout;

	/**
	 * @param fileName
	 */
	public <E> TopographyReader(String fileName) {
		super();
		this.fileName = fileName;

        this.graphFile = new File(fileName);
        this.impl = GraphMLImporter.createFromFile(this.graphFile);

        this.impl.nodeAttributeHandler(new AttributeHandler<String>() {
            @Override
            public void handle(String obj, String id, AttributeGetter getter) {
            	/*
                System.out.println("label: " + getter.has(String.class, "label"));
                System.out.println("Latitude: " + getter.get(Double.class, "Latitude"));
                System.out.println("Longitude: " + getter.get(Double.class, "Longitude"));
                System.out.println("label: " + getter.get(String.class, "label"));
                System.out.println("Country: " + getter.get(String.class, "Country"));
                System.out.println("id: " + id);
                System.out.println();
                */
            }
        });

	}


	/**
	 *
	 */
	public void createGraph() {
		//g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		g = new ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Map<String, String> map = new HashMap<String, String>();
        impl.generateGraph(/*(Graph<String, E>)*/ g, new ContinuousString(), map);

        vertexList = g.vertexSet().toArray();

        //System.out.println("graph=" + g);
	}


	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}


	/**
	 * @return the graphFile
	 */
	public File getGraphFile() {
		return graphFile;
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
	 * @return the impl
	 */
	public GraphMLImporter<String, DefaultWeightedEdge> getImpl() {
		return impl;
	}


	/**
	 * @return the g
	 */
	//public SimpleWeightedGraph<String, DefaultWeightedEdge> getG() {
	public Graph<String, DefaultWeightedEdge> getG() {
		return g;
	}


	/**
	 * @param g the g to set
	 */
	//public void setG(SimpleWeightedGraph<String, DefaultWeightedEdge> g) {
	public void setG(ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge> g) {
		this.g = g;
	}


	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	/**
	 * @param graphFile the graphFile to set
	 */
	public void setGraphFile(File graphFile) {
		this.graphFile = graphFile;
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
	 * @param impl the impl to set
	 */
	public void setImpl(GraphMLImporter<String, DefaultWeightedEdge> impl) {
		this.impl = impl;
	}


	public static JGraphLayout getLayout() {
		return layout;
	}


	public static void setLayout(JGraphLayout layout) {
		TopographyReader.layout = layout;
	}

	/**
	 * Print vertex list in the command line
	 * @param pathsTree
	 */
	public void printVertexList(SingleSourcePaths<String, DefaultWeightedEdge> pathsTree) {
        vertexList = pathsTree.getGraph().vertexSet().toArray();
        //Print out vertexList
        System.out.println("\nVertex List\n");
        for(int i = 0; i < vertexList.length; i++) {
        	System.out.println(vertexList[i].toString());
        }
	}


	/**
	 * Analyze the network graph using Dijkstra's shortest path algorithm
	 * @param vertex Array of vertex list represented as a string
	 */
	public void dijkstraTable(String vertex) {
		GraphPath<String, DefaultWeightedEdge> dijkPath;
        List<DefaultWeightedEdge> pathWalk;
        SingleSourcePaths<String, DefaultWeightedEdge> pathsTree =
        		new DijkstraShortestPath<String, DefaultWeightedEdge>(g).getPaths(vertex);
        String[] hops;

        //Print vertex list for testing
        //printVertexList(pathsTree);

        FlowTable table = new FlowTable();
        table.addRow("Switch Port", "MAC src", "MAC dst", "Ether Src","Ether Type","Vlan ID", "IP Src","IP Dest", "TCP Src","TCP Dest","IP Proto","Action");
        table.addRow("-----------", "-------", "-------", "---------", "---------", "------", "------", "------", "-------", "------", "-------", "-----");

        for(int i = 0; i < vertexList.length; i++) {
        	dijkPath = pathsTree.getPath(vertexList[i].toString());
        	pathWalk = dijkPath.getEdgeList();

        	if(!dijkPath.getStartVertex().equals(dijkPath.getEndVertex())) {
        		//System.out.printf("\n%s",dijkPath.getStartVertex());
        		hops = pathWalk.get(0).toString().split(":");
        		table.addRow(
        				"*", //Switch Port
        				"*", //MAC Src
        				"*", //MAC Type
        				"*", //Ether Src
        				"*", //Ether Type
        				"*", //VLAN ID
        				dijkPath.getStartVertex(), //IP Src
        				dijkPath.getEndVertex(),  //IP Dest
        				"*", //TCP Src
        				"*", //TCP Dest
        				"*", //IP Proto
        				hops[1].substring(0, hops[1].length()-1).trim()); //Action
        	}
        }
        System.out.println(table.toString());
	}


	/**
	 * Analyze the network graph using the Bellman-Ford shortest path algorithm
	 * @param vertex Array of vertex list represented as a string
	 */
	/**
	 * @param vertex
	 */
	public void bellmanFordTable(String vertex) {
		GraphPath<String, DefaultWeightedEdge> bellmanPath;
        List<DefaultWeightedEdge> pathWalk;
        SingleSourcePaths<String, DefaultWeightedEdge> pathsTree =
        		new BellmanFordShortestPath<String, DefaultWeightedEdge>(g).getPaths(vertex);
        String[] hops;

        FlowTable table = new FlowTable();
        table.addRow("Switch Port", "MAC src", "MAC dst", "Ether Src","Ether Type","Vlan ID", "IP Src","IP Dest", "TCP Dst","TCP Src","IP Proto","Action");
        table.addRow("-----------", "-------", "-------", "---------", "---------", "------", "------", "------", "-------", "------", "-------", "-----");

        for(int i = 0; i < vertexList.length; i++) {
        	bellmanPath = pathsTree.getPath(vertexList[i].toString());
        	pathWalk = bellmanPath.getEdgeList();

        	if(!bellmanPath.getStartVertex().equals(bellmanPath.getEndVertex())) {
        		//System.out.printf("\n%s",dijkPath.getStartVertex());
        		hops = pathWalk.get(0).toString().split(":");
        		table.addRow("*",
        				"*",
        				"*",
        				"*",
        				"*",
        				"*",
        				bellmanPath.getStartVertex(),
        				bellmanPath.getEndVertex(),
        				"*",
        				"*",
        				"*", hops[1].substring(0, hops[1].length()-1).trim());
        	}
        }
        System.out.println(table.toString());
	}

	/**
	 * Handle command line arguments using Apache CLI
	 * @param args Command line arguments
	 * @return
	 */
	public CommandLine handleCommandArgs(String[] args) {
		CommandLine line = null;
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create the Options
		Options options = new Options();
		Option help = new Option("help", "print this message");
		options.addOption( help );

		Option alg = Option.builder("alg")
                .hasArg()
                .desc( "Select a shortest path algorithm "
                		+ "-d: use Dijkstra's shortest path algorithm "
                		+ "-b: use Bellman-Ford shortest path algorithm ")
                .valueSeparator('=')
                .build();
		options.addOption(alg);

		Option layout = Option.builder("layout")
                .hasArg()
                .desc( "Select a shortest path algorithm "
                		+ "-h: use hierarchical network diagram layout "
                		+ "-c: use circle network diagram layout ")
                .valueSeparator('=')
                .build();
		options.addOption(layout);

        String header = "Generate a network diagram from a GraphML file\n\n";
        String footer = "\n";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("topographyreader", header, options, footer, true);

        try {
            // parse the command line arguments
            line = parser.parse( options, args );
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }
        return line;
	}

	/**
	 * @param args[0] Name of the GraphML file to process
	 * @param args[1] Switch for handling shortest path algorithm.
	 * 				  -alg:0 - Dijkstra shortest path algorithm
	 * 				  -alg:1 - Bellman-Ford shortest path algorithm
	 * @param args[2] Switch for handling visualization of the network graph
	 * 				  -layout:0 - Hierarchical layout
	 * 				  -layout:1 - Circle layout
	 * @throws ImportException
	 * @throws Exception
	 */
	public static <E> void main(String[] args) throws ImportException, Exception {

		//Create from file
		//TopographyReader<E> topReader = new TopographyReader<E>("./files/Internet2.graphml");
		TopographyReader<E> topReader = new TopographyReader<E>(args[0]);

		/*
		 * View the list of submitted arguments using Apache CLI
		 *
		CommandLine commands = topReader.handleCommandArgs(args);
		String[] argList = commands.getOptionValues("alg");
		System.out.printf("\nArguments: ");
		for(String arg : argList)
			System.out.printf("\t%s",arg);
		System.out.printf("\n\n");
		*/

				topReader.createGraph();

		for(String arg : args) {
			if(arg.contains("alg:0"))
				topReader.dijkstraTable(topReader.vertexList[0].toString());
			else if(arg.contains("alg:1"))
				topReader.bellmanFordTable(topReader.vertexList[0].toString());
			else if(arg.contains("layout:0"))
				setLayout(new JGraphHierarchicalLayout());
			else if(arg.contains("layout:1"))
				setLayout(new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE, topReader.getWidth(), topReader.getHeight()));
			else {
				//Do nothing
			}
		}



		//If switch /alg:0 then generate table using Dijkstra's shortest path algorithm
		topReader.dijkstraTable(topReader.vertexList[0].toString());
		//topReader.bellmanFordTable(topReader.vertexList[0].toString());

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(topReader.getWidth(), topReader.getHeight());
		frame.setBounds(10, 10, topReader.getWidth() * 2, topReader.getHeight() * 2);
		JGraph jgraph = new JGraph(new JGraphModelAdapter<String, DefaultWeightedEdge>(topReader.getG()));
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
