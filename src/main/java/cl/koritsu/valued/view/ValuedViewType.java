package cl.koritsu.valued.view;

import cl.koritsu.valued.view.busqueda.BusquedaTasacionesView;
import cl.koritsu.valued.view.dashboard.DashboardView;
import cl.koritsu.valued.view.facturacion.FacturacionView;
import cl.koritsu.valued.view.nuevatasacion.NuevaTasacionView;
import cl.koritsu.valued.view.reports.ReportsView;
import cl.koritsu.valued.view.schedule.AdministrationView;
import cl.koritsu.valued.view.transactions.MisSolicitudesView;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum ValuedViewType {
    DASHBOARD(DashboardView.NAME/*"reportería"*/, DashboardView.class, FontAwesome.PIE_CHART, true), 
    SALES(NuevaTasacionView.NAME/*"nueva solicitud"*/, NuevaTasacionView.class, FontAwesome.BUILDING, false), 
    TRANSACTIONS2(MisSolicitudesView.NAME/*"tasaciones en curso"*/, MisSolicitudesView.class, FontAwesome.TABLE, false),
    BUSQUEDA_TASACIONES(BusquedaTasacionesView.NAME/*"Buscar Tasación"*/, BusquedaTasacionesView.class, FontAwesome.SEARCH, true),
    FACTURACION(FacturacionView.NAME/*"Facturación"*/, FacturacionView.class, FontAwesome.DOLLAR, true),
    REPORTS(ReportsView.NAME/*"Buscar Tasación"*/, ReportsView.class, FontAwesome.SIGNAL, true), 
    ADMINISTRATION(AdministrationView.NAME/*"Administración"*/, AdministrationView.class, FontAwesome.GEAR, false),
    ;

    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;

    private ValuedViewType(final String viewName,
            final Class<? extends View> viewClass, final Resource icon,
            final boolean stateful) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static ValuedViewType getByViewName(final String viewName) {
        ValuedViewType result = null;
        for (ValuedViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
