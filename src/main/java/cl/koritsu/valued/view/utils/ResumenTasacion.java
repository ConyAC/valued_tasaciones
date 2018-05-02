package cl.koritsu.valued.view.utils;

import java.util.Set;

import cl.koritsu.valued.domain.ObraComplementaria;
import cl.koritsu.valued.domain.ProgramaBien;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.services.ValuedService;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class ResumenTasacion extends FormLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5035241314130096444L;
    public static final String ID = "resumenwindow";	
    private String apiKey="AIzaSyBUxpPki9NJFg10wosJrH0Moqp1_JzsNuo";
    GoogleMap googleMap;
    ValuedService service;
    FormLayout windows;
	
	public ResumenTasacion(final SolicitudTasacion sol, ValuedService service, boolean ubicacion, boolean window) {
    	init(sol, service, ubicacion, window);
    }
	
	 public void init(SolicitudTasacion s, ValuedService service, boolean ubi, boolean window) {
	        //addStyleName("profile-window");
	    	this.service = service;
	        setId(ID);
	        Responsive.makeResponsive(this);
			setSizeFull();
			
			if(window)
				buildResumenWindow(s, true);
			else{
				addComponent(buildResumen(s,ubi));
				setWidth("530px");
				setHeight("350px");
			}
			
	 }
	 
	 private Window buildResumenWindow(SolicitudTasacion sol, boolean ubicacion){			
		 Window window = new Window(sol.getNumeroTasacion());
		 window.setHeight("500px");
	     window.setModal(true);
		 window.setResizable(false);
		 window.center();
		 
		 window.setContent(buildResumen(sol, ubicacion));
		 UI.getCurrent().addWindow(window);
		 
		 return window;
		 	
	 }	 
	 
	 private VerticalLayout buildResumen(SolicitudTasacion sol, boolean ubicacion) {
		 
		VerticalLayout vl = new VerticalLayout();        
		FormLayout fl = new FormLayout();
		vl.addComponent(fl);
		fl.setMargin(true);
		
      	Label sectionCliente = new Label("Resumen");
      	sectionCliente.addStyleName(ValoTheme.LABEL_H3);
      	sectionCliente.addStyleName(ValoTheme.LABEL_COLORED);	    
      	fl.addComponent(sectionCliente);
	    
	    Label encargo = new Label();
	    encargo.setCaption("Fecha Encargo");
	    encargo.setValue(Utils.formatoFecha(sol.getFechaEncargo()));
	    fl.addComponent(encargo);
	    
	    Label cliente = new Label();
	    cliente.setCaption("Nombre Cliente");
	    cliente.setValue(sol.getCliente().getFullname());
	    fl.addComponent(cliente);
	    
	    Label estado = new Label();
	    estado.setCaption("Estado");
	    estado.setValue(sol.getEstado().name());
	    fl.addComponent(estado);
	    
	    Label informe = new Label();
	    informe.setCaption("Tipo Informe");
	    informe.setValue(sol.getTipoInformeString());
	    fl.addComponent(informe);
	    
	    Label clase = new Label();
	    clase.setCaption("Clase Bien");
	    clase.setValue(sol.getBien().getClase().toString());
	    fl.addComponent(clase);
	    
	    Label direccion = new Label();
	    direccion.setCaption("Dirección");
	    direccion.setValue(sol.getDireccionCompleta());
	    fl.addComponent(direccion);
	    
	 	Label sectionTasacion = new Label("Información Tasación");
	 	sectionTasacion.addStyleName(ValoTheme.LABEL_H3);
	 	sectionTasacion.addStyleName(ValoTheme.LABEL_COLORED);	    
	 	fl.addComponent(sectionTasacion);
	    
	    Label tasador = new Label();
	    tasador.setCaption("Tasador");
	    tasador.setValue(sol.getTasador().getFullname());
	    fl.addComponent(tasador);
	    
	    if(sol.getFechaTasacion() != null){
		    Label visita = new Label();
		    visita.setCaption("Fecha Visita");
		    visita.setValue(Utils.formatoFecha(sol.getFechaTasacion()));
		    fl.addComponent(visita);
	    }
	    
	    if(sol.getFechaVisado() != null){
		    Label visado = new Label();
		    visado.setCaption("Fecha Visado");
		    visado.setValue(Utils.formatoFecha(sol.getFechaVisado()));
		    fl.addComponent(visado);
	    }
	    
	    if(sol.getFechaEnvioCliente() != null){
		    Label envioCl = new Label();
		    envioCl.setCaption("Fecha Envio a Cliente");
		    envioCl.setValue(Utils.formatoFecha(sol.getFechaEnvioCliente()));
		    fl.addComponent(envioCl);
	    }
	    
	    Label spTerreno = new Label();
	    spTerreno.setCaption("Mts Superficie Terreno");
	    spTerreno.setValue(Float.toString(sol.getBien().getSuperficieTerreno()));
	    fl.addComponent(spTerreno);
	    
	    Label valorTerreno = new Label();
	    valorTerreno.setCaption("Valor UF Superficie Terreno");
	    valorTerreno.setValue(Float.toString(sol.getBien().getUfSuperficieTerreno()));
	    fl.addComponent(valorTerreno);
	    
	    Label spEdificado = new Label();
	    spEdificado.setCaption("Mts Superficie Edificado");
	    spEdificado.setValue(Float.toString(sol.getBien().getSuperficieConstruida()));
	    fl.addComponent(spEdificado);
	    
	    Label valorEdificado = new Label();
	    valorEdificado.setCaption("Valor UF Superficie TerEdificadoreno");
	    valorEdificado.setValue(Float.toString(sol.getBien().getUfSuperficieConstruida()));
	    fl.addComponent(valorEdificado);
	    
	    Label spTerraza = new Label();
	    spTerraza.setCaption("Superficie Balcón/Terraza");
	    spTerraza.setValue(Float.toString(sol.getBien().getSuperficieTerraza()));
	    fl.addComponent(spTerraza);
	    
	    Label valorTerraza = new Label();
	    valorTerraza.setCaption("Valor UF Superficie Balcón/Terraza");
	    valorTerraza.setValue(Float.toString(sol.getBien().getUfSuperficieTerraza()));
	    fl.addComponent(valorTerraza);
	    
	    Label sectionAdicionales = new Label("Adicionales");
	    sectionAdicionales.addStyleName(ValoTheme.LABEL_H3);
	    sectionAdicionales.addStyleName(ValoTheme.LABEL_COLORED);	    
	    fl.addComponent(sectionAdicionales);
	    
	    if(!sol.getBien().getObrasComplementarias().isEmpty()){
	    	Set<ObraComplementaria> obras = sol.getBien().getObrasComplementarias();
	    	for(ObraComplementaria o : obras){
    		   Label obra = new Label();
    		   obra.setCaption(o.getAdicional().name());
    		   obra.setValue("Superficie "+Long.toString(o.getCantidadSuperficie())+", Valor UF "+Long.toString(o.getValorTotalUF()));
    		   fl.addComponent(obra);
	    	}	    	
	    }else{
	    	Label mensaje1 = new Label();
	    	mensaje1.setValue("No registra adicionales.");
	    	fl.addComponent(mensaje1);
	    }
	    
	    Label sectionPRograma = new Label("Programa");
	    sectionPRograma.addStyleName(ValoTheme.LABEL_H3);
	    sectionPRograma.addStyleName(ValoTheme.LABEL_COLORED);	    
	    fl.addComponent(sectionPRograma);

	    if(!sol.getBien().getProgramas().isEmpty()){
	    	Set<ProgramaBien> programas = sol.getBien().getProgramas();
	    	for(ProgramaBien p : programas){
    		   Label programa = new Label();
    		   programa.setCaption(p.getPrograma().name());
    		   programa.setValue("Cantidad/Superficie "+Float.toString(p.getCantidadSuperficie()));
    		   fl.addComponent(programa);
	    	}	    	
	    }else{
	    	Label mensaje1 = new Label();
	    	mensaje1.setValue("No registra programas.");
	    	fl.addComponent(mensaje1);
	    }
	    
	    if(sol.getObservacionReparo() != null){	    	
		    Label sectionObs = new Label("Observaciones Reparo");
		    sectionObs.addStyleName(ValoTheme.LABEL_H3);
		    sectionObs.addStyleName(ValoTheme.LABEL_COLORED);
		    fl.addComponent(sectionObs);
		    
		    Label obs = new Label();
		    obs.setCaption("Observación Reparo");
		    obs.setValue(sol.getObservacionReparo());
		    fl.addComponent(obs);
	    }
	    
	    if(ubicacion){	    	
		    Label sectionUbicacion = new Label("Ubicación");
		    sectionUbicacion.addStyleName(ValoTheme.LABEL_H3);
		    sectionUbicacion.addStyleName(ValoTheme.LABEL_COLORED);
		    fl.addComponent(sectionUbicacion);
		    
	    	vl.addComponent(buildUbicacion(sol));
	    }
	    
	   // vl.addComponent(buildFooter());		

		return vl;
	}

	 private HorizontalLayout buildFooter() {
	    HorizontalLayout footer = new HorizontalLayout();
	    footer.setSpacing(true);
	    footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
	   // footer.setWidth(100.0f, Unit.PERCENTAGE);
	    
	    Button btnCerrar = new Button("Cerrar");
	    btnCerrar.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				//((UI)window.getParent()).removeWindow(window);
			}
		});
	    
	    btnCerrar.setIcon(FontAwesome.CLOSE);
	    btnCerrar.addStyleName(ValoTheme.BUTTON_PRIMARY);	    
	    
	    footer.addComponent(btnCerrar);
	    footer.setComponentAlignment(btnCerrar, Alignment.BOTTOM_RIGHT);
	    
	    return footer;
	}
	 
	 private VerticalLayout buildUbicacion(SolicitudTasacion sol){		
		VerticalLayout vl = new VerticalLayout();
		vl.setCaption("Mapa");
		vl.setSizeFull();
		
		googleMap = new GoogleMap(apiKey, null, "english");
		googleMap.setCenter(new LatLon(sol.getNorteY(), sol.getEsteX()));
		googleMap.addMarker(sol.getEstado().name(), new LatLon(sol.getNorteY(), sol.getEsteX()), false, "VAADIN/img/pin_tas_asignada.png");
		googleMap.setSizeFull();	
		googleMap.setZoom(20);
		
		vl.addComponent(googleMap);
		
		return vl;
	}
}
