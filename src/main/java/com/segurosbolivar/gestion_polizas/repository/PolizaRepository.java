
package com.segurosbolivar.gestion_polizas.repository;

import com.segurosbolivar.gestion_polizas.entity.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findByTipoAndEstado(String tipo, String estado);

    List<Poliza> findByTipo(String tipo);

    List<Poliza> findByEstado(String estado);
}
