package controllers;

import conexion.SQLConexion;
import conexion.SQLConexion2;
import java.awt.BorderLayout;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import modelos.Usuario;

@ManagedBean
@RequestScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 8792997835541614151L;

    private Usuario usuario = null;
    SQLConexion2 con = new SQLConexion2();

    public LoginController() {
    }

    @PostConstruct
    public void init() {
        this.usuario = new Usuario();
        con.conectar();
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date obtenerFecha() {
        Calendar cal = Calendar.getInstance();
        Date fecha = cal.getTime();
        TimeZone timezone = TimeZone.getTimeZone("America/Guatemala");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        sdf.setTimeZone(timezone);
        String sDate = sdf.format(fecha);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Date newDate = null;
        try {
            newDate = sdf2.parse(sDate);
        } catch (ParseException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newDate;
    }

    public String login() {
        String redireccion = null;
        if (this.usuario.getUser().trim().equals("")
                || this.usuario.getPassword().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "LLene los campos", ""));
        } else {
            String query = "SELECT * FROM FAC_Usuarios where Usuario='"+usuario.getUser()+"' and Password='"+usuario.getPassword()+"' and Status='A'";
            ResultSet result = con.consulta(query);
            Usuario user=null;
            try {
                if(result.next()){
                    user = new Usuario();
                    user.setNombre(result.getString(2));
                    user.setUser(usuario.getUser());
                    user.setPassword(usuario.getPassword());
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
            con.cerrar();
            if (user != null) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", user);      
                    redireccion = "pretty:inicio";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datos incorrectos", ""));
            }
        }
        return redireccion;
    }

    public void logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigator = context.getApplication().getNavigationHandler();
        navigator.handleNavigation(context, null, "pretty:login");
    }
}
