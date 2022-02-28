import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

public class project4main {
	
	public static void main(String[] args) {
		
		File inFile = new File(args[0]);
		File outFile = new File(args[1]);
		
		Scanner reader;
		try{
			reader = new Scanner(inFile);
		}
		catch(FileNotFoundException e){
			System.out.println("Cannot find input file");
			return;
		}
		
		PrintStream outStream;
		try {
			outFile.createNewFile();
			
			outStream = new PrintStream(outFile);
		}
		catch(IOException e){
			e.printStackTrace();
			reader.close();
			return;
		}
		
		
		//Reading input data
		
		int greenTrainNum = Integer.valueOf(reader.nextLine());
		int[] greenTrains = new int[greenTrainNum];//This list contains capacities of greenTrains
		for(int i=0;i<greenTrainNum;i++) {
			greenTrains[i] = reader.nextInt();
		}
		reader.nextLine();
		
		int redTrainNum = Integer.valueOf(reader.nextLine());
		int[] redTrains = new int[redTrainNum];
		for(int i=0;i<redTrainNum;i++) {
			redTrains[i] = reader.nextInt();
		}
		reader.nextLine();
		
		int greenDeerNum = Integer.valueOf(reader.nextLine());
		int[] greenDeers = new int[greenDeerNum];
		for(int i=0;i<greenDeerNum;i++) {
			greenDeers[i] = reader.nextInt();
		}
		reader.nextLine();
		
		int redDeerNum = Integer.valueOf(reader.nextLine().strip());
		int[] redDeers = new int[redDeerNum];
		for(int i=0;i<redDeerNum;i++) {
			redDeers[i] = reader.nextInt();
		}
		reader.nextLine();
		
		int totalGifts = 0;//Total gifts inside bags.Will be used to output minimum unsent gifts.
		
		int bagNum = Integer.valueOf(reader.nextLine());
		HashMap<Integer,String> idToType = new HashMap<Integer,String>(); // A HashMap for bags that take id return type
		HashMap<Integer,Integer> idToCapacity = new HashMap<Integer,Integer>();// A HashMap for bags that take id return total gifts inside
		
		int currentId = 1;
		int[] typeCapacities = new int[8];// In order to unite the bag types that not include a inside , we will store every type for one slot
		
		for(int i=1;i<=bagNum;i++) {
			String type = reader.next();
			int capacity = reader.nextInt();
			
			if(Arrays.asList("b","c","d","e","bd","be","cd","ce").contains(type)) {
				typeCapacities[(Arrays.asList("b","c","d","e","bd","be","cd","ce").indexOf(type))] += capacity;
				totalGifts += capacity;
				continue;
			}
			
			idToType.put(currentId,type); // bags will take ids starting from 1
			idToCapacity.put(currentId, capacity);
			totalGifts += capacity;
			currentId++;
		}
		int lastId = idToType.keySet().size(); 
		for(int i=1;i<=8;i++) { // since the non a types were collected together they will get one id for each type which makes 8 ids
			idToCapacity.put((lastId+i),typeCapacities[i-1]);
			idToType.put((lastId+i),(Arrays.asList("b","c","d","e","bd","be","cd","ce").get(i-1)));
		}
		
		GiftGraph gg = new GiftGraph(greenDeers,redDeers,greenTrains,redTrains,idToType,idToCapacity);
		gg.constructGraph();
		int answer = totalGifts - gg.solve();
		outStream.print(answer);
		
		
		reader.close();
		outStream.close();
	}
}
