package androidpro.com.general;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AcaoAtiva {
    private String uid;
    private String ticker;
    private String nomeAcao;
    private String segmento;
    private boolean prazo;
    private String qtdComprada;
    private String alertaVenda;
    private String totalInvestido;
    private String precoCompra;
    private String perdaGanhoP;
    private String perdaGanhoR;
    private String saldoAtual;
    private String dataCompra;
    private String valorAtual;
    private String valorAnterior;
    private Map<String, String> weekly;
    private Map<String, String> monthly;
    private Map<String, String> meses;

    public AcaoAtiva(){ }

    public AcaoAtiva (String uid, String ticker, String nomeAcao, String precoCompra,
                      boolean prazo, String qtdComprada, String alertaVenda) {

        this.uid = uid;
        this.ticker = ticker;
        this.nomeAcao = nomeAcao;
        this.prazo = prazo;
        this.precoCompra = precoCompra;
        this.qtdComprada = qtdComprada;
        this.alertaVenda = alertaVenda;

        if(!setStaticData())
            throw new RuntimeException();

        UpdateAcao.updateDynamicData(this);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public boolean isPrazo() {
        return prazo;
    }

    public void setPrazo(boolean prazo) {
        this.prazo = prazo;
    }

    public String getQtdComprada() {
        return qtdComprada;
    }

    public void setQtdComprada(String qtdComprada) {
        this.qtdComprada = qtdComprada;
    }

    public String getAlertaVenda() {
        return alertaVenda;
    }

    public void setAlertaVenda(String alertaVenda) {
        this.alertaVenda = alertaVenda;
    }

    public String getTotalInvestido() {
        return totalInvestido;
    }

    public void setTotalInvestido(String totalInvestido) {
        this.totalInvestido = totalInvestido;
    }

    public String getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(String precoCompra) {
        this.precoCompra = precoCompra;
    }

    public String getPerdaGanhoP() {
        return perdaGanhoP;
    }

    public void setPerdaGanhoP(String perdaGanhoP) {
        this.perdaGanhoP = perdaGanhoP;
    }

    public String getPerdaGanhoR() {
        return perdaGanhoR;
    }

    public void setPerdaGanhoR(String perdaGanhoR) {
        this.perdaGanhoR = perdaGanhoR;
    }

    public String getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(String saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public String getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(String dataCompra) {
        this.dataCompra = dataCompra;
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

    public boolean setStaticData() {
        JSONObject overview = AcaoData.getOverview(this.ticker);

        if(!overview.has("Symbol"))  //Checa se ticker é válido
            return false;

        try {
            this.setSegmento(overview.getString("Sector"));
        } catch (JSONException e){
            e.printStackTrace();
        }

        double totalInvestido = Integer.parseInt(this.getQtdComprada()) * Double.parseDouble(this.getPrecoCompra());
        this.setTotalInvestido(String.valueOf(totalInvestido));

        SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
        this.setDataCompra(formatData.format(new Date()));

        return true;
    }
}
