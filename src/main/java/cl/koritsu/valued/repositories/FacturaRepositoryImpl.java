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

import cl.koritsu.valued.domain.Factura;
import cl.koritsu.valued.view.busqueda.BuscarSolicitudVO;

public class FacturaRepositoryImpl implements FacturaRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    
	@Override	
	public List<Factura> findFacturas(BuscarSolicitudVO vo) {
		
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Factura> query = cb.createQuery(Factura.class);

        Root<Factura> root = query.from(Factura.class);
        List<Predicate> list = new ArrayList<Predicate>();
        
        if (!vo.getNroFactura().isEmpty()) {
            list.add(cb.equal(root.get("numero"),vo.getNroFactura()));
        }

        if (vo.getEstado() != null) {
            list.add(cb.equal(root.get("estado"),vo.getEstado()));
        }

        if(vo.getCliente() != null){
        	list.add(cb.equal(root.get("cliente"),vo.getCliente()));
        }
        
        if (vo.getDireccion() != null) {
            list.add(cb.equal(root.get("solicitud").get("bien").get("direccion"),vo.getDireccion()));
        }
        
        if(vo.getComuna() != null){
        	list.add(cb.equal(root.get("solicitud").get("bien").get("comuna"),vo.getComuna()));
        }
                
        if(vo.getRegion() != null){
        	list.add(cb.equal(root.get("solicitud").get("bien").get("comuna").get("region"),vo.getRegion()));
        }        
        
        query.where(list.toArray(new Predicate[list.size()]));
        query.orderBy(cb.desc(root.get("fecha")));

        try {
            return em.createQuery(query).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
