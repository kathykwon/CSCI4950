/**
 *  Class for generating a formatted flow table
 */


/**
 * @author Reese Troup
 *
 */
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FlowTable{
    List<String[]> rows = new LinkedList<String[]>();
 
    public void addRow(String... cols)
    {
        rows.add(cols);
    }
 
    private int[] columnWidth()
    {
        int cols = -1;
 
        for(String[] row : rows)
            cols = Math.max(cols, row.length);
 
        int[] widths = new int[cols];
 
        for(String[] row : rows) {
            for(int colNum = 0; colNum < row.length; colNum++) {
                widths[colNum] =
                    Math.max(
                        widths[colNum],
                        StringUtils.length(row[colNum]));
            }
        }
 
        return widths;
    }
    
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
 
        int[] colWidths = columnWidth();
 
        for(String[] row : rows) {
            for(int colNum = 0; colNum < row.length; colNum++) {
            	buffer.append(
                    StringUtils.rightPad(
                        StringUtils.defaultString(
                            row[colNum]), colWidths[colNum]));
            	buffer.append(' ');
            }
 
            buffer.append('\n');
        }
 
        return buffer.toString();
    }
}