package Firstplayer;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc) 
	{
		while(true)
		{
			if(rc.getType()==RobotType.HQ) //if i'm a headquarters
			{
				Direction spawnDir = Direction.NORTH;
				try {
					if(rc.isActive()&&rc.canMove(spawnDir)&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
						rc.spawn(spawnDir);
					}
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(rc.getType()==RobotType.SOLDIER)
			{

				Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam().opponent());
				if(enemyRobots.length>0)
				{
					Robot anEnemy = enemyRobots[0];
					RobotInfo anEnemyInfo = null;
					try {
						anEnemyInfo = rc.senseRobotInfo(anEnemy);
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(anEnemyInfo.location.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared)
					{
						if(rc.isActive())
						{
							try {
								rc.attackSquare(anEnemyInfo.location);
							} catch (GameActionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				else
				{
					if(Math.random()<.01)
					{
						if(rc.isActive())
						{
							try {
								rc.construct(RobotType.PASTR);
							} catch (GameActionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				Direction allDirections[] = Direction.values();
				Direction chosenDirection = allDirections[(int)(Math.random()*8)];
				if(rc.isActive()&&rc.canMove(chosenDirection))
				{
					try {
						rc.move(chosenDirection);
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			rc.yield();
		}
		

	}
}