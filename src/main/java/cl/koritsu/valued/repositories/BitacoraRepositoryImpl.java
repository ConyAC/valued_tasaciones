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

import cl.koritsu.valued.domain.Bitacora;
import cl.koritsu.valued.view.bitacora.BuscarBitacoraSolicitudVO;

public class BitacoraRepositoryImpl implements BitacoraRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    
	@Override	
	public List<Bitacora> findBitacora(BuscarBitacoraSolicitudVO vo) {
		
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Bitacora> query = cb.createQuery(Bitacora.class);

        Root<Bitacora> root = query.from(Bitacora.class);
        List<Predicate> list = new ArrayList<Predicate>();
        
        if (!vo.getNroTasacion().isEmpty()) {
            list.add(cb.equal(root.get("solicitudTasacion").get("numeroTasacion"),vo.getNroTasacion())); //list.add(cb.equal(root.get("solicitud").get("bien").get("comuna").get("region"),vo.getRegion()));
        }

        if (vo.getEtapaTasacion() != null) {
            list.add(cb.equal(root.get("etapaTasacion"),vo.getEtapaTasacion()));
        }

        if(vo.getCliente() != null){
        	list.add(cb.equal(root.get("solicitudTasacion").get("cliente"),vo.getCliente()));
        }
                        
        if(vo.getUsuario() != null){
        	list.add(cb.equal(root.get("usuario"),vo.getUsuario()));
        }
        
        if(vo.getFechaInicio() != null){
        	list.add(cb.equal(root.get("fechaInicio"),vo.getFechaInicio()));
        }
        
        if(vo.getFechaTermino() != null){
        	list.add(cb.equal(root.get("fechaTermino"),vo.getFechaTermino()));
        } 
        
        query.where(list.toArray(new Predicate[list.size()]));
        query.orderBy(cb.desc(root.get("fechaInicio")));

        try {
            return em.createQuery(query).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
