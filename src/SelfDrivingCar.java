
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class SelfDrivingCar {
    public static void main (String[] args) {

        int numOfVehicles;
        int numOfRides;
        int numSteps;
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        ArrayList<Ride> rides = new ArrayList<Ride>();
        BufferedReader bufferedreader = null;
        List<String> textLines = new ArrayList<String>();
        String[] split;

        try {
            FileInputStream fstream = new FileInputStream("C:\\Users\\olayinka\\Downloads\\e_high_bonus.in");
            bufferedreader = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = bufferedreader.readLine()) != null) {
                textLines.add(strLine);
            }
            bufferedreader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] values = textLines.get(0).split("\\s+");
        numOfVehicles = Integer.parseInt(values[2]);
        numOfRides = Integer.parseInt(values[3]);
        numSteps = Integer.parseInt(values[5]);
        String[] vals;
        for (int i = 1; i < textLines.size(); i++) {
            vals = textLines.get(i).split("\\s+");
            Ride newRide = new Ride (i-1, Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Integer.parseInt(vals[2]), Integer.parseInt(vals[3]),
                    Integer.parseInt(vals[4]), Integer.parseInt(vals[5]));

            rides.add(newRide);
        }

        for (int  i = 0;  i < numOfVehicles; i++) {
            map.put (i,assignedRidesToAVehicle(rides));
        }

        for (int  i = 0;  i < numOfVehicles; i++) {
            String s = "";
            s += map.get(i).size() + " ";
            for (int j = 0; j < map.get(i).size(); j++) {
                s+= map.get(i).get(j) + " ";
            }
            System.out.println(s);
        }
    }


    public static ArrayList<Integer> assignedRidesToAVehicle(List<Ride> rides) {
        PriorityQueue<RideDistance> queue = new PriorityQueue<RideDistance>();
        long steps = 0;
        ArrayList<Integer> chosenRides = new ArrayList<Integer>();
        for (Ride ride : rides) {
            long distToNewRide = ride.starti + ride.startj;
            queue.add(new RideDistance(ride, distToNewRide+ ride.distance, ride.distance - distToNewRide));
        }
        RideDistance current = null;
        while(!queue.isEmpty()) {
            current = queue.poll();

            long len = current.totaldist;
            if(len < current.ride.latestFinish && !current.ride.visited)
                break;
        }

        if(current == null || current.ride.visited) return chosenRides;

        chosenRides.add(current.ride.RideNum);
        steps = current.ride.earliestTime + current.totaldist;
        current.ride.visited = true;
        long index = 0;
        while (index < rides.size()) {
            PriorityQueue<RideDistance> queu = new  PriorityQueue<RideDistance>();
            for (Ride ride : rides) {
                if(!ride.visited) {
                    long distToNewRide = Math.abs(current.ride.endi - ride.starti) + Math.abs(current.ride.endj  - ride.startj);
                    queu.add(new RideDistance(ride,  distToNewRide + ride.distance, ride.distance - distToNewRide));

                }
            }

            current = queu.peek();
            while(!queu.isEmpty()) {
                current = queu.poll();
                Long len = current.totaldist;

                if(steps + len < current.ride.latestFinish && steps + len >= current.ride.earliestTime) {

                    chosenRides.add(current.ride.RideNum);
                    current.ride.visited = true;
                    steps += len;
                    break;
                }

            }

            index++;
        }
        return chosenRides;
    }


    public static class Ride {
        int RideNum;
        int earliestTime;
        int latestFinish;
        int starti, startj;
        int endi, endj;
        int distance;
        boolean visited;

        Ride (int _rideNum, int _starti, int _startj, int _endi, int _endj, int _startTime,int _endTime) {
            RideNum = _rideNum;
            earliestTime = _startTime;
            latestFinish = _endTime;
            starti = _starti;
            startj = _startj;
            endi = _endi;
            endj = _endj;
            visited = false;
            distance = (Math.abs(endi - starti) + Math.abs(endj - startj));

        }
    }

    public static class RideDistance implements Comparable<RideDistance>{
        Ride ride;
        long totaldist;
        long adjustedweight;

        RideDistance(Ride _ride,long _totaldist, long _adjustedweight ) {
            ride = _ride;
            totaldist = _totaldist;
            adjustedweight = _adjustedweight;
        }

        @Override
        public int compareTo(RideDistance o) {

            return  Long.compare(o.adjustedweight, adjustedweight);
        }

    }
}