package watson;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer
{
	public static RobotController rc;
	static Direction allDirections[] = Direction.values();
	static Random rand = new Random();
	static MapLocation myLoc;
	static int directionalLooks[] = new int[]{0, -1, 1, -2, 2};
	public static void run(RobotController rcin) 
	{
		rc = rcin;
		rand.setSeed(rc.getRobot().getID());
		while(true)
		{			
			myLoc = rc.getLocation();
			try{
				if(rc.getType()==RobotType.HQ) //if i'm a headquarters
				{
					runHeadquarters();
				}
				else if(rc.getType()==RobotType.SOLDIER)
				{
					runsoldier();
				}
				rc.yield();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void runsoldier() throws GameActionException {
		tryToShoot();
		//communication 
		rc.setIndicatorString(0, "read ID:"+rc.readBroadcast(0));
		int editingChannel = Clock.getRoundNum()%2;
		int usingChannel = (Clock.getRoundNum()+1)%2;

		int runningTotal = rc.readBroadcast(editingChannel);
		rc.broadcast(editingChannel, runningTotal+1);
		
		MapLocation runningVectorTotal = intToLoc(rc.readBroadcast(editingChannel+2));
		rc.broadcast(editingChannel+2, locToInt(mladd(runningVectorTotal, myLoc)));
		MapLocation averagePositionOfSwarm = mldivide(intToLoc(rc.readBroadcast(usingChannel+2)), rc.readBroadcast(usingChannel));
		
		rc.setIndicatorString(0, ""+locToInt(averagePositionOfSwarm));
		
//		Direction chosenDirection = allDirections[(int)(rand.nextDouble()*8)];
//		if(rc.isActive()&&rc.canMove(chosenDirection))
//		{
//			rc.move(chosenDirection);
//		}
		swarmMove(averagePositionOfSwarm);
	}
	
	private static void swarmMove(MapLocation averagePositionOfSwarm) throws GameActionException
	{
		Direction chosenDirection = rc.getLocation().directionTo(averagePositionOfSwarm);
		if(rc.isActive())
		{
			if(rand.nextDouble()<.5)
			{
				for(int directionalOffset:directionalLooks)
				{
					int forwardInt = chosenDirection.ordinal();
					Direction trialdir = allDirections[(forwardInt + directionalOffset+8)%8];
					if(rc.canMove(trialdir))
					{
						rc.move(trialdir);
						break;
					}	
				}
			}
			else
			{
				Direction d = allDirections[(int)(rand.nextDouble()*8)];
				if(rc.isActive()&&rc.canMove(d))
				{
					rc.move(d);
				}
			}
		}
	}
	
	private static MapLocation mladd(MapLocation a, MapLocation b)
	{
		return new MapLocation(a.x + b.x, a.y+b.y);
	}
	
	private static MapLocation mldivide(MapLocation bigM, int divisor)
	{
		return new MapLocation(bigM.x/divisor, bigM.y/divisor);
	}
	
	private static int locToInt(MapLocation m){
		return (m.x*100 + m.y);
	}
	
	private static MapLocation intToLoc(int i)
	{
		return new MapLocation(i/100, i%100);
	}

	private static void tryToShoot() throws GameActionException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam().opponent());
		if(enemyRobots.length>0)
		{
			Robot anEnemy = enemyRobots[0];
			RobotInfo anEnemyInfo = null;
			anEnemyInfo = rc.senseRobotInfo(anEnemy);
			if(anEnemyInfo.location.distanceSquaredTo(myLoc)<rc.getType().attackRadiusMaxSquared)
			{
				if(rc.isActive())
				{
					rc.attackSquare(anEnemyInfo.location);
				}
			}
		}
		else
		{
			if(rand.nextDouble()<.001&&rc.sensePastrLocations(rc.getTeam()).length<5)
			{
				//rc.senseCowsAtLocation();
				if(rc.isActive())
				{
					rc.construct(RobotType.PASTR);

				}
			}
		}
	}

	private static void runHeadquarters() throws GameActionException {
		// TODO Auto-generated method stub
		Direction spawnDir = Direction.NORTH;
		if(rc.isActive()&&rc.canMove(spawnDir)&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
			rc.spawn(spawnDir);
		}
		int editingChannel = Clock.getRoundNum()%2;
		int usingChannel = (Clock.getRoundNum()+1)%2;
		rc.broadcast(editingChannel, 0);
		rc.broadcast(editingChannel+2, 0);
	}
}