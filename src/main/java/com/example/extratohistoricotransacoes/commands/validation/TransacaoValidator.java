public class TransacaoValidator {
    public void validar(Transacao transacao);
    private void validarCamposObrigatorios(Transacao transacao);
    private void validarConta(Integer conta);
    private void validarValor(BigDecimal valor);
    private void validarTipo(TipoTransacaoEnum tipo);
    public void validarAtualizacao(Transacao atual, Transacao nova);
}