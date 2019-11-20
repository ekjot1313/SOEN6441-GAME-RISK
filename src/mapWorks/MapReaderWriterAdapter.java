package mapWorks;

import java.io.File;
import java.io.IOException;

import dao.Map;

public class MapReaderWriterAdapter extends DominationReaderWriter {
	ConquestReaderWriter crw = new ConquestReaderWriter();
	
	public MapReaderWriterAdapter(ConquestReaderWriter crw){
		this.crw =  crw;
	}

	@Override
	public int parseMapFile(Map map,File file) {
		// TODO Auto-generated method stub
		return crw.parseMapFile(map,file);
	}

	@Override
	public void saveMap(Map map, String fileName) throws IOException {
		// TODO Auto-generated method stub
		crw.saveMap(map, fileName);
	}
	
	
}