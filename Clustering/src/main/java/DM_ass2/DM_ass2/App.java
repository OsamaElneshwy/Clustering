package DM_ass2.DM_ass2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class App 
{
	public static Vector<Points> allData = new Vector<Points>();
	public static Vector<Cluster> table = new Vector<Cluster>();
	static int index = 0;

	
    public static void main( String[] args ) throws IOException
    {
    	
    	int numOfClusters = 0;
    	Scanner input = new Scanner(System.in);
    	System.out.print("Enter the Number of Clusters: ");
    	numOfClusters = input.nextInt();
    	
    	loadData("F:\\CourseEvaluation.xlsx");
    	randomCentroids( numOfClusters );
    	cluster();
    	getOutlier();
    	
    	for( int i = 0; i < table.size() ; i++ )
    	{
    			
    		if( i == index )
    		{
    			System.out.print("size: "+table.get(i).Individual.size()+" Outlier: [");
    		}
    		else
    		{
    			System.out.println("clusterName: "+table.get(i).clusterName);
        		System.out.println("size: "+table.get(i).Individual.size()+" , centroid: "+ table.get(i).centroid);
    			System.out.print("Individual: [");
    		}
    		for( int k =0 ; k < table.get(i).Individual.size() ; k ++)
    		{
    			System.out.print(table.get(i).Individual.get(k).transID+",");
    		}
    		System.out.println("]");
    		System.out.println("******************************************");
    	}
    	

    }
    
    
    public static void loadData(String path) throws IOException
    {
    	File excelFile = new File( path );
        FileInputStream fis = new FileInputStream(excelFile);      
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet= workbook.getSheetAt(0);       
        Iterator<Row> rowit = sheet.iterator();
        rowit.next();        
        while( rowit.hasNext() )
        {
        	Row row = rowit.next();
        	Iterator<Cell> cellit = row.cellIterator(); 
        	
        	Points x = new Points();
        	int position = 0;
        	
        	while( cellit.hasNext() )
        	{
        		Cell cell = cellit.next();
        		if( position == 0)
        		{
        			x.transID = cell.toString();
        			position++;
        		}
        		else
        		{
        			x.point.add( Double.parseDouble(cell.toString()) );
        		}		
        	}
        	position = 0;
        	allData.add(x);
        } 
        workbook.close();
        fis.close();
    }
    
    
    public static void randomCentroids( int num )
    {
    	Vector<Integer> uniqueCentroids = new Vector<Integer>();
    	Random rand = new Random();
    	int cen = 0;
    	while( num > 0 )
    	{
    		cen = rand.nextInt(allData.size() - 0 + 1) + 0;
    		if( !uniqueCentroids.contains(cen) )
    		{
    			uniqueCentroids.add(cen);
    			num--;
    		}
    	}
    	for( int i = 0; i < uniqueCentroids.size() ; i++ )
    	{
    		Cluster x = new Cluster();
    		x.clusterName = "cluster"+(i+1)+"";
    		x.centroid = allData.get( uniqueCentroids.get( i ) ).point;   
    		table.add(x);
    	}
    }
    
    
    public static int getDistance( Vector<Double> point )
    {
    	double min = 1000.0;
    	Vector<Double> value = new Vector<Double>();
    	for( int j = 0; j < table.size(); j++ )
    	{
    		double distance = 0.0;
    		for(int k = 0; k < table.get(0).centroid.size(); k++ )
    		{
    			distance += Math.pow( point.get(k) - table.get(j).centroid.get(k) , 2 );
    		}
    		distance = Math.sqrt(distance);
    		value.add(distance);
    	}
    	
    	for( int i = 0 ; i < value.size() ; i++ )
    	{
    		if( value.get(i) < min )
    		{
    			min = value.get(i);
    		}
    	}
    	return value.indexOf(min);
    }
    
    
    public static void cluster()
    {
    	int index = 0 ,check = 0;
    	boolean flag = false , change = false;
    	for( int i = 0 ; i < allData.size() ; i++ )
    	{
    		index = getDistance( allData.get(i).point );
    		table.get(index).Individual.add( allData.get(i));	
    	}
    	while( true )
    	{       		
    		for( int i = 0; i < table.size() ; i++ )
    		{
    			Cluster x = new Cluster();
    			for( int j = 0; j < table.get(i).centroid.size() ; j++ )
        		{
    				double coordMian = 0.0;
    				for( int k = 0; k < table.get(i).Individual.size() ; k++ )
            		{
    					coordMian += table.get(i).Individual.get(k).point.get(j);
            		}
    				coordMian = coordMian / table.get(i).Individual.size();
    				x.clusterName = table.get(i).clusterName;
    				x.centroid.add(coordMian);
        		}
    			if( !table.get(i).centroid.equals(x.centroid) )
    			{
    				change = true;
    				table.set(i,x);
    			}
    			else if( table.get(i).centroid.equals(x.centroid) )
    			{
    				check++;
    			}
    		    			
    			if( check == table.size() )
    			{
    				flag = true;
    				check = 0;
    				break;
    			}
    		}  
    		
    		if( change )
			{
				for( int v = 0; v < table.size(); v++ )
				{
					table.get(v).Individual.clear();
				}
				
				for( int i = 0 ; i < allData.size() ; i++ )
	        	{
	        		index = getDistance( allData.get(i).point );
	        		table.get(index).Individual.add( allData.get(i));	
	        	}
				
			}
    		
    		if( flag )
    		{
    			break;
    		}
    		
    	}
    	
    }
    
    
    public static void getOutlier()
    {
    	int min = 1000;
    	for( int i = 0 ; i < table.size() ; i++ )
    	{
    		if( table.get(i).Individual.size() < min && table.get(i).Individual.size() != 0 )
    		{
    			min = table.get(i).Individual.size();
    			index = i;
    		}
    	}
    }
    
}






