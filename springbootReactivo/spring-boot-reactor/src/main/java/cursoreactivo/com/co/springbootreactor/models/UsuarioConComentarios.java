package cursoreactivo.com.co.springbootreactor.models;

public class UsuarioConComentarios {

    private Usuario usuario;
    private Comentario comentario;

    public UsuarioConComentarios(Usuario usuario, Comentario comentario) {
        this.usuario = usuario;
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "UsuarioConComentarios[" +
                usuario +
                " , " + comentario +
                ']';
    }
}
