package br.com.itarocha.importadorjdbc;

public class Config {
	
	private Boolean aplicarCommit;
	private Boolean gerarMetaData;
	private Boolean umArquivoPorTabela;
	private Boolean todasAsTabelas;
	private String path;
	private String fileNameMetadata;
	private String fileNameData;

	public Config(){
		this.aplicarCommit = true;
		this.gerarMetaData = false;
		this.umArquivoPorTabela = false;
		this.todasAsTabelas = true;
		this.path = "c:\\temp\\";
		this.fileNameMetadata = "metadata";
		this.fileNameData = "data";
	}
	

	public Boolean getAplicarCommit() {
		return aplicarCommit;
	}
	public void setAplicarCommit(Boolean aplicarCommit) {
		this.aplicarCommit = aplicarCommit;
	}
	public Boolean getGerarMetaData() {
		return gerarMetaData;
	}
	public void setGerarMetaData(Boolean gerarMetaData) {
		this.gerarMetaData = gerarMetaData;
	}
	public Boolean getUmArquivoPorTabela() {
		return umArquivoPorTabela;
	}
	public void setUmArquivoPorTabela(Boolean umArquivoPorTabela) {
		this.umArquivoPorTabela = umArquivoPorTabela;
	}
	public Boolean getTodasAsTabelas() {
		return todasAsTabelas;
	}
	public void setTodasAsTabelas(Boolean todasAsTabelas) {
		this.todasAsTabelas = todasAsTabelas;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFileNameMetadata() {
		return fileNameMetadata;
	}
	public void setFileNameMetadata(String value) {
		this.fileNameMetadata = value;
	}
	public String getFileNameData() {
		return fileNameData;
	}
	public void setFileNameData(String value) {
		this.fileNameData = value;
	}
}
