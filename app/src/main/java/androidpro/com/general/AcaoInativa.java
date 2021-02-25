package androidpro.com.general;

import org.json.JSONObject;
import java.util.Map;

public class AcaoInativa {
    private String ticker;
    private String nomeAcao;
    private String segmento;
    private boolean prazo;
    private String alertaCompra;
    private String valorAtual;
    private String valorAnterior;
    private String nAcoes;
    private String valorMercado;
    private Map<String, String> weekly;
    private Map<String, String> monthly;
    private Map<String, String> meses;

    public AcaoInativa(){ }

    public AcaoInativa(String ticker, String nomeAcao, String alertaCompra, boolean prazo) {
        this.ticker = ticker;
        this.nomeAcao = nomeAcao;
        this.prazo = prazo;
        this.alertaCompra = alertaCompra;

        if(!setStaticData())
            throw new RuntimeException();

        UpdateAcao.updateDynamicData(this);
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getNomeAcao() {
        return nomeAcao;
    }

    public void setNomeAcao(String nomeAcao) {
        this.nomeAcao = nomeAcao;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getAlertaCompra() {
        return alertaCompra;
    }

    public void setAlertaCompra(String alertaCompra) {
        this.alertaCompra = alertaCompra;
    }

    public String getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(String valorAtual) {
        this.valorAtual = valorAtual;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getnAcoes() {
        return nAcoes;
    }

    public void setnAcoes(String nAcoes) {
        this.nAcoes = nAcoes;
    }

    public String getValorMercado() {
        return valorMercado;
    }

    public void setValorMercado(String valorMercado) {
        this.valorMercado = valorMercado;
    }

    public boolean isPrazo() {
        return prazo;
    }

    public void setPrazo(boolean prazo) {
        this.prazo = prazo;
    }

    public Map<String, String> getWeekly() {
        return weekly;
    }

    public void setWeekly(Map<String, String> weekly) {
        this.weekly = weekly;
    }

    public Map<String, String> getMonthly() {
        return monthly;
    }

    public void setMonthly(Map<String, String> monthly) {
        this.monthly = monthly;
    }

    public Map<String, String> getMeses() {
        return meses;
    }

    public void setMeses(Map<String, String> meses) {
        this.meses = meses;
    }

    public boolean setStaticData(){
        JSONObject overview = AcaoData.getOverview(this.ticker);

        if(!overview.has("Symbol"))  //Checa se ticker é válido
            return false;

        try {
            this.setSegmento(overview.getString("Sector"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}


