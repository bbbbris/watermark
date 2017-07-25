package watermark.extract.util;

import java.util.Comparator;

public class SpaceComparator implements Comparator<Space> {

	@Override
	public int compare(Space o1, Space o2) {
		if(o1.getLength() < o2.getLength()){
			return -1;
		}
		if(o1.getLength() > o2.getLength()){
			return 1;
		}
		return 0;
	}

}
