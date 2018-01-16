package cl.koritsu.valued.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.hibernate.validator.constraints.Email;

import cl.koritsu.valued.domain.enums.EstadoUsuario;

@Entity
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private boolean tasador;
    private String nombres = "";
    private String apellidoPaterno = "";
    private String apellidoMaterno = "";
    private boolean male;
    @Email(message="El email es inválido.")
    private String email = "";
    private EstadoUsuario habilitado;
    private String contrasena = "";
    private String telefonoFijo;
    private String telefonoMovil;
    private String nroCuentaBancaria;
    private String banco;
    @JoinColumn(name="rolId")
    private Rol rol;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isTasador() {
		return tasador;
	}

	public void setTasador(boolean tasador) {
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

	public EstadoUsuario getHabilitado() {
		return habilitado;
	}

	public void setHabilitado(EstadoUsuario habilitado) {
		this.habilitado = habilitado;
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
