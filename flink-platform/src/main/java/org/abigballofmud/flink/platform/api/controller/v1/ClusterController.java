package org.abigballofmud.flink.platform.api.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.abigballofmud.flink.platform.api.dto.ClusterDTO;
import org.abigballofmud.flink.platform.domain.entity.Cluster;
import org.abigballofmud.flink.platform.domain.repository.ClusterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/28 1:14
 * @since 1.0
 */
@RestController("clusterController.v1")
@RequestMapping("/v1/{tenantId}/cluster")
public class ClusterController {

    private final ClusterRepository clusterRepository;

    public ClusterController(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }

    @GetMapping
    public IPage<ClusterDTO> list(@PathVariable Long tenantId,
                                  ClusterDTO clusterDTO,
                                  Page<Cluster> clusterPage) {
        clusterDTO.setTenantId(tenantId);
        clusterPage.addOrder(OrderItem.desc(Cluster.FIELD_CLUSTER_ID));
        return clusterRepository.pageAndSortDTO(clusterDTO, clusterPage);
    }
}
