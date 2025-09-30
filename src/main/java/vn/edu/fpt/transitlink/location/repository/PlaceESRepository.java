package vn.edu.fpt.transitlink.location.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vn.edu.fpt.transitlink.location.entity.PlaceDocument;

import java.util.List;
import java.util.UUID;

public interface PlaceESRepository extends ElasticsearchRepository<PlaceDocument, UUID> {
    @Query("""
            {
              "match": {
                "name": {
                  "query": "?0",
                  "operator": "and"
                }
              }
            }
            """)
    List<PlaceDocument> findByNameMatch(String name);
}
