package info.hannes.mechadminGen.rest;

public class MxAdmin extends AbstractMxAdmin {
	
	public MxAdmin(String baseUrl){
		super(baseUrl, false);
	}
	
	public MxAdmin(String baseUrl, boolean debug){
		super(baseUrl, debug);
	}
}
