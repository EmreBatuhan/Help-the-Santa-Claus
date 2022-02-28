import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;

public class GiftGraph {

	int[] greenDeers; // These four lists store capacities of the transporters
	int[] redDeers;
	int[] greenTrains;
	int[] redTrains;
	
	HashMap<Integer,String> idToType; // A HashMap for bags that take id return type
	HashMap<Integer,Integer> idToCapacity; // A HashMap for bags that take id return total gifts inside
	
	ArrayList<Integer> greenDeerIds = new ArrayList<Integer>(); // These lists store ids of the transports
	ArrayList<Integer> redDeerIds = new ArrayList<Integer>();
	ArrayList<Integer> greenTrainIds = new ArrayList<Integer>();
	ArrayList<Integer> redTrainIds = new ArrayList<Integer>();
	
	int sinkId; 
	
	int totalNodes;
	int[][] roadMatrix; //roadMatrix stores road lengths for each road
	
	//This HashMap will store suitable destinations for each node id.By suitable we mean destination is at next level and has a road between them
	HashMap<Integer,HashSet<Integer>> idToDestinations = new HashMap<Integer,HashSet<Integer>>();
	
	//The last foundPath from source to sink 
	ArrayList<Integer> foundPath = new ArrayList<Integer>();
	
	
	public GiftGraph(int[] greenDeers,int[] redDeers,int[] greenTrains,int[] redTrains,HashMap<Integer,String> idToType,HashMap<Integer,Integer> idToCapacity) {
		this.greenDeers = greenDeers;
		this.redDeers = redDeers;
		this.greenTrains = greenTrains;
		this.redTrains = redTrains;
		this.idToType = idToType;
		this.idToCapacity = idToCapacity;
		
		int id = idToType.keySet().size();
		for(int i=0;i<greenDeers.length;i++) { // Ids are given to each transporter. 
			id += 1;
			greenDeerIds.add(id);
		}
		for(int i=0;i<redDeers.length;i++) {
			id += 1;
			redDeerIds.add(id);
		}
		for(int i=0;i<greenTrains.length;i++) {
			id += 1;
			greenTrainIds.add(id);
		}
		for(int i=0;i<redTrains.length;i++) {
			id += 1;
			redTrainIds.add(id);
		}
		id += 1;
		sinkId = id;
		
		//    greenDeer quantity + redDeer quantity + greenTrain quantity + redTrain quantity + giftBag quantity + source & sink
		this.totalNodes = greenDeers.length + redDeers.length + greenTrains.length + redTrains.length + idToType.keySet().size() + 2;
		
		this.roadMatrix = new int[totalNodes][totalNodes];
	}
	
	public void constructGraph() {
		
		int totalBagNum = idToType.keySet().size();
		
		for(int i=1; i <= totalBagNum;i++) {
			
			String type = idToType.get(i);
			int flowToTrans = idToCapacity.get(i); // This will be the capacity of the edge between bag and transporter.
			ArrayList<Integer> suitableTrans = new ArrayList<Integer>(); // These transports will have an edge with the bag
			
			roadMatrix[0][i] = idToCapacity.get(i); // Road between source and bag. Road capacity equals bag capacity-gift count-
			
			if(type.contains("a")) {
				flowToTrans = 1; // If bag is type a, capacity from this bag to each transporter should be 1
			}
			
			if(Arrays.asList("a","b","e","be","ab","ae","abe").contains(type)) {
				suitableTrans.addAll(greenDeerIds);
			}
			if(Arrays.asList("a","c","e","ce","ac","ae","ace").contains(type)) {
				suitableTrans.addAll(redDeerIds);
			}
			if(Arrays.asList("a","b","d","bd","ab","ad","abd").contains(type)) {
				suitableTrans.addAll(greenTrainIds);
			}
			if(Arrays.asList("a","c","d","cd","ac","ad","acd").contains(type)) {
				suitableTrans.addAll(redTrainIds);
			}
			
			for(int j=0;j < suitableTrans.size();j++) {
				roadMatrix[i][suitableTrans.get(j)] = flowToTrans; // Adding a road between bags and suitable transports
			}
			
		}
		
		
		// Lastly add the edges between sink and transports
		int id= totalBagNum;
		for(int i=0;i<greenDeers.length;i++) {
			id += 1;
			roadMatrix[id][sinkId] = greenDeers[i];
		}
		for(int i=0;i<redDeers.length;i++) {
			id += 1;
			roadMatrix[id][sinkId] = redDeers[i];
		}
		for(int i=0;i<greenTrains.length;i++) {
			id += 1;
			roadMatrix[id][sinkId] = greenTrains[i];
		}
		for(int i=0;i<redTrains.length;i++) {
			id += 1;
			roadMatrix[id][sinkId] = redTrains[i];
		}
		
	}
	//Dinc's algorithm is used
	public int solve() {
		int result = 0;
		while(true) {
			//BFS part
			
			HashSet<Integer> inside = new HashSet<Integer>(); //This list contains currentLevel ids
			HashSet<Integer> outside = new HashSet<Integer>();//This list contains the ids that haven't been reached yet in bfs
			
			inside.add(0);
			for(int i=1;i<totalNodes;i++) {
				outside.add(i);
			}
			int totalLevel = 1;
			boolean sinkFound = false;
			while(inside.isEmpty() == false && sinkFound == false) {
				HashSet<Integer> inside2 = new HashSet<Integer>();// This list is nextLevel ids
				for(int insider:inside) {
					HashSet<Integer> destinationList = new HashSet<Integer>();
					for(int outsider:outside) {
						if(roadMatrix[insider][outsider] > 0 && outsider == sinkId) {						
							destinationList = new HashSet<Integer>();
							destinationList.add(sinkId);
							idToDestinations.put(insider, destinationList);
							sinkFound = true;
							inside2.add(sinkId);
							break;
						}
						if(roadMatrix[insider][outsider] > 0) {
							
							destinationList.add(outsider);
							inside2.add(outsider);
						}
					}
					idToDestinations.put(insider, destinationList);
				}

				totalLevel++;
				inside = new HashSet<Integer>();
				for(int insider:inside2) {
					outside.remove(insider);
					inside.add(insider);
				}
			}
			if(inside.isEmpty()) {//If no node was reached for the next Level we finish the algorithm
				break;
			}
		
			//DFS part
			ArrayList<Integer> currentPath = new ArrayList<Integer>();
			currentPath.add(0);
			
			while(this.findPath(totalLevel,0, 0, currentPath)) {
				int minCapacity = roadMatrix[foundPath.get(0)][foundPath.get(1)];
				
				//Finding the minimum capacity in the foundPath
				for(int i=0;i+1<foundPath.size();i++) {
					if(roadMatrix[foundPath.get(i)][foundPath.get(i+1)] < minCapacity) {
						minCapacity = roadMatrix[foundPath.get(i)][foundPath.get(i+1)];
					}
				}
				//Now we traverse the road as supposed to be done in Ford–Fulkerson algorithm
				for(int i=0;i+1<foundPath.size();i++) {
					roadMatrix[foundPath.get(i)][foundPath.get(i+1)] -= minCapacity;
					roadMatrix[foundPath.get(i+1)][foundPath.get(i)] += minCapacity;
					// If the road's capacity got reduced to 0, then it is no longer a suitable destination
					if(roadMatrix[foundPath.get(i)][foundPath.get(i+1)] <= 0) {
						idToDestinations.get(foundPath.get(i)).remove(foundPath.get(i+1));
					}
				}
				result += minCapacity;
				currentPath = new ArrayList<Integer>();
				currentPath.add(0);	
			}
		}
		return result;
	}
	
	public boolean findPath(int totalLevel ,int currentLevel,int currentNodeId,ArrayList<Integer> currentPath) {
		
		HashSet<Integer> destinationList = idToDestinations.get(currentNodeId);
		
		if(totalLevel < currentLevel + 2) {
			return false;// total level is the level count found in bfs. When we pass it we return.
		}
		
		HashSet<Integer> blackList = new HashSet<Integer>();
		
		for(int i:destinationList) {
				if(i == sinkId) {
					currentPath.add(sinkId);
					
					//When a road is found in recursive method, we don't need anything else because the roads will be modified 
					//So we need to leave the method immediately therefore we save this road and finish the method
					
					foundPath = new ArrayList<Integer>();
					for(int j:currentPath) {
						foundPath.add(j);
					}
					return true;
				}
				currentPath.add(i);
				if(findPath(totalLevel,(currentLevel+1),i,currentPath)) {
					//If going to i was a right decision, we have found a path so we return
					return true;
				}
				else{
					//If going to i didn't result in a path,we don't visit i ever again until the next bfs
					blackList.add(i);
				}
				currentPath.remove(Integer.valueOf(i));
		}
		for(int i:blackList) {
			destinationList.remove(i);
		}
		return false;
	}
}
