package cl.koritsu.valued.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.view.busqueda.BuscarSolicitudVO;

public class SolicitudTasacionRepositoryImpl implements SolicitudTasacionRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    
	@Override	
	public List<SolicitudTasacion> findTasaciones(BuscarSolicitudVO vo) {
		
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SolicitudTasacion> query = cb.createQuery(SolicitudTasacion.class);

        Root<SolicitudTasacion> root = query.from(SolicitudTasacion.class);
        List<Predicate> list = new ArrayList<Predicate>();
        
        if (vo.getNroTasacion() != null && !vo.getNroTasacion().isEmpty()) {
            list.add(cb.equal(root.get("numeroTasacion"),vo.getNroTasacion()));
        }
        
        if (vo.getDireccion() != null) {
            list.add(cb.like(root.get("bien").get("direccion"),vo.getDireccion().getDireccion()));
        }

        if (vo.getEstado() != null) {
            list.add(cb.equal(root.get("estado"),vo.getEstado()));
        }

        if(vo.getCliente() != null){
        	list.add(cb.equal(root.get("cliente"),vo.getCliente()));
        }
        
        if(vo.getTasador() != null){
        	list.add(cb.equal(root.get("tasador"),vo.getTasador()));
        }
        
        if(vo.getComuna() != null){
        	list.add(cb.equal(root.get("bien").get("comuna"),vo.getComuna()));
        }
                
        if(vo.getRegion() != null){
        	list.add(cb.equal(root.get("bien").get("comuna").get("region"),vo.getRegion()));
        }        
        
        query.where(list.toArray(new Predicate[list.size()]));
        query.orderBy(cb.desc(root.get("fechaEncargo")));

        try {
            return em.createQuery(query).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
