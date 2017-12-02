package cl.koritsu.valued.view.nuevatasacion;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.maddon.ListContainer;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.domain.Bien;
import cl.koritsu.valued.domain.HonorarioCliente;
import cl.koritsu.valued.domain.Movie;
import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;
import cl.koritsu.valued.services.ValuedService;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = NuevaTasacionView.NAME, cached = true)
public class NuevaTasacionView extends VerticalLayout implements View, WizardProgressListener {

	public static final String NAME = "nueva_solicitud";
	
	@Autowired
	ValuedService service;
	
    private ComboBox movieSelect;
    
    private Wizard wizard;
    private BeanFieldGroup<SolicitudTasacion> fg = new BeanFieldGroup<SolicitudTasacion>(SolicitudTasacion.class);

    public NuevaTasacionView() {

    }
    
    @PostConstruct
    public void init() {
        setSizeFull();
        addStyleName("sales");

        addComponent(buildHeader());
        
        VerticalLayout vl = buildContent();
        addComponent(vl);
        setExpandRatio(vl, 1);
    }

    private VerticalLayout buildContent() {
    	
    	 wizard = new Wizard();
    	 wizard.setSizeFull();
    	 
         wizard.setUriFragmentEnabled(true);
         wizard.addListener(this);
         wizard.getBackButton().setCaption("Anterior");
         wizard.getNextButton().setCaption("Siguiente");
         wizard.getFinishButton().setCaption("Finalizar");
         wizard.getCancelButton().setCaption("Cancelar");
         
         SolicitudTasacion nuevaTasacion = new SolicitudTasacion();
         nuevaTasacion.setBien(new Bien());
         nuevaTasacion.setHonorarioCliente(new HonorarioCliente());
         nuevaTasacion.setUsuario((Usuario) VaadinSession.getCurrent().getAttribute(Usuario.class.getName()));
         fg.setItemDataSource(nuevaTasacion);
         
         wizard.addStep(new ClienteStep(fg,service), "cliente");
         wizard.addStep(new BienStep(fg,service), "bien");
         wizard.addStep(new SolTasacionStep(fg,service), "solicitud");
         wizard.addStep(new HonorarioClienteStep(wizard,fg,service), "ingreso");
         wizard.setSizeFull();
		return new VerticalLayout(){
			{
				setSizeFull();
				addComponent(wizard);
			}
		};
	}

	private void initMovieSelect() {
        Collection<Movie> movies = ValuedUI.getDataProvider().getMovies();
        Container movieContainer = new ListContainer<Movie>(Movie.class, movies);
        movieSelect.setContainerDataSource(movieContainer);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label titleLabel = new Label("Nueva Tasación");
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }



    @Override
    public void enter(final ViewChangeEvent event) {
    	if(fg.isModified()) {
    		reset() ;
    	}
    	wizard.setVisible(true);
    }
    
    private void reset() {
    	fg.discard();
		//TODO confimar que se perderán los cambios
        SolicitudTasacion nuevaTasacion = new SolicitudTasacion();
        nuevaTasacion.setBien(new Bien());
        nuevaTasacion.setHonorarioCliente(new HonorarioCliente());
        nuevaTasacion.setUsuario((Usuario) VaadinSession.getCurrent().getAttribute(Usuario.class.getName()));
        fg.setItemDataSource(nuevaTasacion);
        //retorna a la primera etapa
        while(!wizard.isActive(wizard.getSteps().get(0)))
        	wizard.back();
    }

	@Override
	public void activeStepChanged(WizardStepActivationEvent event) {
		Page.getCurrent().setTitle(event.getActivatedStep().getCaption());
	}

	@Override
	public void stepSetChanged(WizardStepSetChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wizardCompleted(WizardCompletedEvent event) {
		
		//intenta guardar
		try {
			fg.commit();
			//realiza todas las validaciones que puedan falta
			//TODO
			//crea el objeto de solicitud tasacion
			String nroValued = service.saveSolicitud(fg.getItemDataSource().getBean());
			endWizard("Se ha ingresado la solicitud con el siguiente número asociado "+nroValued);
			
			reset();
		}catch(CommitException ce) {
			ce.printStackTrace();
			Notification.show("Error al guardar la nueva solicitud debido : "+ce.getMessage(),Type.ERROR_MESSAGE);
		}
		
	}



	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		endWizard("Wizard Cancelado");
	}
	
    private void endWizard(String message) {
        wizard.setVisible(false);
        Notification notification = new Notification( message, Type.HUMANIZED_MESSAGE );
        notification.setDelayMsec(( int ) TimeUnit.SECONDS.toMillis( 7 ));
        notification.show(Page.getCurrent());
        
        Page.getCurrent().setTitle(message);

    }
}
