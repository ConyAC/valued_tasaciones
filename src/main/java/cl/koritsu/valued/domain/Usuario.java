package cl.koritsu.valued.domain;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import cl.koritsu.valued.domain.enums.EstadoUsuario;

@Entity
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Boolean tasador = Boolean.FALSE;
    private String nombres = "";
    private String apellidoPaterno = "";
    private String apellidoMaterno = "";
    private boolean male;
    @Email(message="El email es inválido.")
    private String email = "";
    
    @Convert(converter = EstadoUsuarioConverter.class)
    @Column(name = "estadoUsuario", nullable=false)
    @NotNull(message="Es necesario definir el estado")
    private EstadoUsuario estadoUsuario = EstadoUsuario.HABILITADO ;
    
    private String contrasena = "";
    private String telefonoFijo;
    private String telefonoMovil;
    private String nroCuentaBancaria;
    private String banco;
    @JoinColumn(name="rolId")
    private Rol rol;
    
    /**
     * Obliga a que status sea activo, si no viene uno seteado
     */
    @PrePersist
    void preInsert() {
       if(estadoUsuario == null)
    	   estadoUsuario = EstadoUsuario.HABILITADO;
       if(tasador == null)
    	   tasador = Boolean.FALSE;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getTasador() {
		return tasador;
	}

	public void setTasador(Boolean tasador) {
		this.tasador = tasador;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidoPaterno() {
		return apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public EstadoUsuario getEstadoUsuario() {
		return estadoUsuario;
	}

	public void setEstadoUsuario(EstadoUsuario estadoUsuario) {
		this.estadoUsuario = estadoUsuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getTelefonoFijo() {
		return telefonoFijo;
	}

	public void setTelefonoFijo(String telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}

	public String getTelefonoMovil() {
		return telefonoMovil;
	}

	public void setTelefonoMovil(String telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}

	public String getNroCuentaBancaria() {
		return nroCuentaBancaria;
	}

	public void setNroCuentaBancaria(String nroCuentaBancaria) {
		this.nroCuentaBancaria = nroCuentaBancaria;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}
	
	//atributo para crear el registro
    transient String contrasena2;
    
	public String getContrasena2() {
		return contrasena2;
	}

	public void setContrasena2(String contrasena2) {
		this.contrasena2 = contrasena2;
	}


	public String getFullname(){
    	return (nombres != null ? nombres : "") + " " + (apellidoPaterno != null ? apellidoPaterno : "");
    }    
    
}
