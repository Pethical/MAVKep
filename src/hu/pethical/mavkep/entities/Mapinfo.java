
package hu.pethical.mavkep.entities;

import java.util.List;

public class Mapinfo{
   	private List<TrainInfo> maps;
   	private Number time;

 	public List<TrainInfo> getMaps(){
		return this.maps;
	}
	public void setMaps(List<TrainInfo> maps){
		this.maps = maps;
	}
 	public Number getTime(){
		return this.time;
	}
	public void setTime(Number time){
		this.time = time;
	}
}
