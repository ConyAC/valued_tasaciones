package cl.koritsu.valued.services;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.Cliente;
import cl.koritsu.valued.domain.Comuna;
import cl.koritsu.valued.domain.HonorarioCliente;
import cl.koritsu.valued.domain.Solicitante;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.TipoInforme;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.domain.enums.ClaseBien;
import cl.koritsu.valued.domain.enums.EstadoSolicitud;
import cl.koritsu.valued.domain.enums.TipoBien;
import cl.koritsu.valued.domain.validator.RutDigitValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/testApplicationContext.xml" })
public class ValuedServiceTest {

	@Autowired
	ValuedService service;
	
	@Test
	public void test() {
		
		try {
			//carga el excel 
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File(
					ValuedServiceTest.class.getResource("/carga.xlsx").toURI())));
			
			Assert.assertNotNull("el wb es nulo", wb);
			
			Sheet sheet = wb.getSheetAt(8);
			Assert.assertNotNull("el sheet es nulo", sheet);
			
			List<SolicitudTasacion> solicitudes = new ArrayList<SolicitudTasacion>( sheet.getLastRowNum());
			
			
			List<String> rutInvalidos = new ArrayList<String>();
			Row row = null;
			//crea una solicitud por cada fila
			for( int i = 0 ; i < sheet.getLastRowNum() + 1 ; i++ ){
				
				if(i == 0) continue; 				
				row = sheet.getRow(i);
				String rut_solicitante = getValueFromCell(row,3);
				String dv_solicitante = getValueFromCell(row,4);	
				//primero valida los rut
				if(rut_solicitante != null && rut_solicitante.length() != 0 && !RutDigitValidator.ValidarRut(Integer.parseInt(rut_solicitante), dv_solicitante.charAt(0)))
					rutInvalidos.add(rut_solicitante);
				
			}
			if(!rutInvalidos.isEmpty()) {
				for(String rutInvalido : rutInvalidos ) {
					System.out.println(rutInvalido+"-"+RutDigitValidator.Digito(Integer.valueOf(rutInvalido)));
				}
				Assert.fail("existe rut invalidos");
			}
			
			for( int i = 0 ; i < sheet.getLastRowNum() + 1 ; i++ ){
				
				row = sheet.getRow(i);
				System.out.println(row.getRowNum());
				
				//ignora la primera fila
				if(i == 0) continue; 				
				int j = 0;
				double cliente =getDoubleFromCell(row,j++);
				String nroEncargo = getValueFromCell(row,j++);
				String nombre_solicitante = getValueFromCell(row,j++);
				String rut_solicitante = getValueFromCell(row,j++);
				String dv_solicitante = getValueFromCell(row,j++);
				String tipoInforme = getValueFromCell(row,j++);
				String tipoBien =getValueFromCell(row,j++);
				double honorarioCliente =getDoubleFromCell(row,j++);
				double honorarioClienteP =getDoubleFromCell(row,j++);
				double honorarioDespl =getDoubleFromCell(row,j++);
				double honorarioTas =getDoubleFromCell(row,j++);
				double honorarioTasPes =getDoubleFromCell(row,j++);
				String ctacte = getValueFromCell(row,j++);
				Date fechaEncargo = getDateFromCell(row,j++);
				Date fechaTasacion =getDateFromCell(row,j++);
				Date fechaEntregaMax =getDateFromCell(row,j++);
				double dias = getDoubleFromCell(row,j++);
				String direccion =getValueFromCell(row,j++);
				String nroDireccion = getValueFromCell(row,j++);
				String comuna =getValueFromCell(row,j++);
				String direccionRegionalSII =getValueFromCell(row,j++);
				String nombreUnidadSII =getValueFromCell(row,j++);
				String direccionUnidadSII =getValueFromCell(row,j++);
				Date fechaEnviada =getDateFromCell(row,j++);
				String obs =getValueFromCell(row,j++);
				String nEncCliente = getValueFromCell(row, j++);
				double montoTasacionUF= getDoubleFromCell(row, j++);
				
				SolicitudTasacion st = new SolicitudTasacion();
				Bien bien = new Bien();
				bien.setClase(ClaseBien.INMUEBLE);
				bien.setTipo(getTipoBien(tipoBien));
				bien.setDireccion(direccion);
				bien.setNumeroManzana(nroDireccion);
				bien.setComuna(getComuna(comuna));
				st.setBien(bien);
				
				Cliente clienteObj = getCliente(cliente);
				st.setCliente(clienteObj);
				
				HonorarioCliente honorarioClienteObj = new HonorarioCliente();
				honorarioClienteObj.setMontoHonorarioUF(Float.parseFloat(honorarioCliente+""));
				honorarioClienteObj.setMontoHonorarioPesos( Float.parseFloat(honorarioClienteP+""));
				st.setHonorarioCliente(honorarioClienteObj);
				
				Solicitante solicitante = new Solicitante();
				setNombresSolicitante(solicitante,nombre_solicitante);
				if(rut_solicitante != null && rut_solicitante.length() > 0 )
					solicitante.setRut(rut_solicitante+"-"+dv_solicitante);
				solicitante.setCliente(clienteObj);
				Solicitante solicitantebd = null;
				try {
					solicitantebd = service.saveSolicitante(solicitante);
				}catch(ConstraintViolationException e) {
					for(ConstraintViolation cv : e.getConstraintViolations())
						System.out.println( cv.getRootBean().getClass().getName()+" "+cv.getPropertyPath()+" "+ cv.getMessage()+" '" + cv.getInvalidValue() + "'" );
					throw e;
				}
				st.setSolicitante(solicitantebd);
				
				st.setNumeroTasacion(nroEncargo);
				st.setEstado(EstadoSolicitud.FACTURA);
				st.setFechaEncargo(fechaEncargo);
				st.setFechaTasacion(fechaTasacion);
				st.setFechaEnvioCliente(fechaEnviada);
				st.setTipoInforme(getTipoInfome(tipoInforme));
				st.setUsuario(getUsuario());
				st.setNumeroTasacionCliente(nEncCliente);
				st.setMontoTasacionUF( Float.parseFloat(montoTasacionUF+""));
				
				System.out.println(row.getRowNum()+" "+nroEncargo);
				//la agrega al arreglo a guardar
				solicitudes.add(st);
				
			}
			Assert.assertNotNull("el row es nulo", wb.getSheetAt(0).getRow(0));
			
			System.out.println(""+wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
			
			try {
			service.saveSolicitudes(solicitudes);
			}catch(ConstraintViolationException e) {
				for(ConstraintViolation cv : e.getConstraintViolations())
					System.out.println( cv.getRootBean().getClass().getName()+" "+cv.getPropertyPath()+" "+ cv.getMessage()+" '" + cv.getInvalidValue() + "'" );
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void cargaTasadores() {
		
		try {
			//carga el excel 
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File(
					ValuedServiceTest.class.getResource("/carga.xlsx").toURI())));
			
			Assert.assertNotNull("el wb es nulo", wb);
			
			Sheet sheet = wb.getSheetAt(9);
			Assert.assertNotNull("el sheet es nulo", sheet);
			
			Row row = null;
			//crea una solicitud por cada fila
			
			for( int i = 0 ; i < sheet.getLastRowNum() + 1 ; i++ ){
				
				row = sheet.getRow(i);
				System.out.println(row.getRowNum());
				
				//ignora la primera fila
				if(i == 0) continue; 				
				int j = 0;
				double cliente =getDoubleFromCell(row,j++);
				String nroEncargo = getValueFromCell(row,j++);
				String nombre_solicitante = getValueFromCell(row,j++);
				String rut_solicitante = getValueFromCell(row,j++);
				String dv_solicitante = getValueFromCell(row,j++);
				String tipoInforme = getValueFromCell(row,j++);
				String tipoBien =getValueFromCell(row,j++);
				double honorarioCliente =getDoubleFromCell(row,j++);
				double honorarioClienteP =getDoubleFromCell(row,j++);
				double honorarioDespl =getDoubleFromCell(row,j++);
				double honorarioTas =getDoubleFromCell(row,j++);
				double honorarioTasPes =getDoubleFromCell(row,j++);
				String ctacte = getValueFromCell(row,j++);
				Date fechaEncargo = getDateFromCell(row,j++);
				Date fechaTasacion =getDateFromCell(row,j++);
				Date fechaEntregaMax =getDateFromCell(row,j++);
				double dias = getDoubleFromCell(row,j++);
				String direccion =getValueFromCell(row,j++);
				String nroDireccion = getValueFromCell(row,j++);
				String comuna =getValueFromCell(row,j++);
				String direccionRegionalSII =getValueFromCell(row,j++);
				String nombreUnidadSII =getValueFromCell(row,j++);
				String direccionUnidadSII =getValueFromCell(row,j++);
				Date fechaEnviada =getDateFromCell(row,j++);
				String obs =getValueFromCell(row,j++);
				String nEncCliente = getValueFromCell(row, j++);
				double montoTasacionUF= getDoubleFromCell(row, j++);
				String tasador= getValueFromCell(row, j++);
				double tasadorId= getDoubleFromCell(row, j++);
				//busca la solicitud asociada al numero de encargo
				SolicitudTasacion st = service.getSolicitudByNumeroTasacion(nroEncargo);
				if(st != null ) {
					if(tasadorId != 0) {
						Usuario usuario = service.getUsuarioById((long) tasadorId);
						if(usuario == null ) fail("el usuario "+tasador+"-"+tasadorId+"no se encontro en la base de datos");
						st.setTasador(usuario);
						service.saveSolicitud(st);
					}
				}
				
			}
			Assert.assertNotNull("el row es nulo", wb.getSheetAt(0).getRow(0));
			
			System.out.println(""+wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double getDoubleFromCell(Row row, int rownum) {
		Cell cell = row.getCell(rownum);
		if(cell == null )return 0;
		if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC )
			return new Double(row.getCell(rownum).getNumericCellValue()).intValue() ;
		else 
			return 0;
	}
	
	private Date getDateFromCell(Row row, int rownum) {
		Cell cell = row.getCell(rownum);
		if(cell == null )return null;
		
		System.out.println(cell);
		
		if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC )
			return row.getCell(rownum).getDateCellValue();
		else 
			return null;
	}

	private String getValueFromCell(Row row, int rownum) {
		Cell cell = row.getCell(rownum);
		if(cell == null )return null;
		if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC )
			return new Double(row.getCell(rownum).getNumericCellValue()).intValue() + "";
		else 
			return row.getCell(rownum).getStringCellValue();
	}

	private void setNombresSolicitante(Solicitante solicitante, String nombre_solicitante) {
		if(nombre_solicitante == null )return;
		String[] splitString = nombre_solicitante.split(" ");
		if(splitString.length > 0 )
			solicitante.setNombres(splitString[0]);
		if(splitString.length > 1 )
			solicitante.setApellidoPaterno(splitString[1]);
		if(splitString.length > 2 )
			solicitante.setApellidoMaterno(splitString[2]);
	}

	private Cliente getCliente(double cliente2) {
		Cliente cliente = new Cliente();
		cliente.setId(new Double(cliente2).longValue());
		return cliente;
	}

	private TipoInforme getTipoInfome(String tipoInforme) {
		TipoInforme tipo = new TipoInforme();
		tipo.setId(1L);
		return tipo;
	}

	private Usuario getUsuario() {
		return service.getUsuarioById(2L);
	}

	private Comuna getComuna(String comuna) {
		return service.getComunaPorNombre(comuna);
	}

	private TipoBien getTipoBien(String tipoBien) {
		tipoBien = tipoBien.toUpperCase();
		if(tipoBien.equals("DEPTO"))
			tipoBien = "DEPARTAMENTO";
		TipoBien tipo = TipoBien.valueOf(tipoBien.replace(" ", "_"));
		if(tipo == null) tipo = TipoBien.OTRO;
		return tipo;
	}
	
	@Test
	public void rutValidatorTest() {
		RutDigitValidator rdv = new RutDigitValidator();
		System.out.println( rdv.isValid("108504852-4", null) );
		System.out.println( rdv.isValid("18504852-4", null) );
		System.out.println( rdv.isValid("10504852-4", null) );
		System.out.println( rdv.isValid("10804852-4", null) );
		System.out.println( rdv.isValid("10854852-4", null) );
		System.out.println( rdv.isValid("10850852-4", null) );
		System.out.println( rdv.isValid("10850452-4", null) );
		System.out.println( rdv.isValid("10850482-4", null) );
		System.out.println( rdv.isValid("10850485-4", null) );
		
	}

}
