package vn.edu.fpt.transitlink.trip.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vn.edu.fpt.transitlink.trip.entity.PassengerJourneyDocument;

import java.util.List;
import java.util.UUID;

public interface PassengerJourneyESRepository extends ElasticsearchRepository<PassengerJourneyDocument, UUID> {

    @Query("""
            {
              "bool": {
                "should": [
                  {
                    "match": {
                      "passengerName": {
                        "query": "?0",
                        "operator": "and",
                        "analyzer": "vi_folded"
                      }
                    }
                  },
                  {
                    "match": {
                      "passengerEmail": {
                        "query": "?0",
                        "operator": "and",
                        "analyzer": "vi_folded"
                      }
                    }
                  }
                ]
              }
            }
            """)
    List<PassengerJourneyDocument> findByPassengerNameOrEmail(String query);

    @Query("""
            {
              "bool": {
                "should": [
                  {
                    "match": {
                      "pickupPlaceName": {
                        "query": "?0",
                        "operator": "and",
                        "analyzer": "vi_folded"
                      }
                    }
                  },
                  {
                    "match": {
                      "dropoffPlaceName": {
                        "query": "?0",
                        "operator": "and",
                        "analyzer": "vi_folded"
                      }
                    }
                  }
                ]
              }
            }
            """)
    List<PassengerJourneyDocument> findByPlaceName(String placeName);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "term": {
                      "status": "?0"
                    }
                  }
                ]
              }
            }
            """)
    List<PassengerJourneyDocument> findByStatus(String status);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "range": {
                      "lastestStopArrivalTime": {
                        "gte": "?0",
                        "lte": "?1"
                      }
                    }
                  }
                ]
              }
            }
            """)
    List<PassengerJourneyDocument> findByDateRange(String startDate, String endDate);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "term": {
                      "passengerId": "?0"
                    }
                  }
                ]
              }
            }
            """)
    List<PassengerJourneyDocument> findByPassengerId(String passengerId);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "bool": {
                      "should": [
                        {
                          "match": {
                            "passengerName": {
                              "query": "?0",
                              "operator": "and",
                              "analyzer": "vi_folded"
                            }
                          }
                        },
                        {
                          "match": {
                            "passengerEmail": {
                              "query": "?0",
                              "operator": "and",
                              "analyzer": "vi_folded"
                            }
                          }
                        },
                        {
                          "match": {
                            "pickupPlaceName": {
                              "query": "?0",
                              "operator": "and",
                              "analyzer": "vi_folded"
                            }
                          }
                        },
                        {
                          "match": {
                            "dropoffPlaceName": {
                              "query": "?0",
                              "operator": "and",
                              "analyzer": "vi_folded"
                            }
                          }
                        }
                      ]
                    }
                  }
                ]
              }
            }
            """)
    List<PassengerJourneyDocument> findByGeneralQuery(String query);

    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "bool": {
                      "should": [
                        {
                          "match": {
                            "passengerName": {
                              "query": "?0",
                              "operator": "and",
                              "analyzer": "vi_folded"
                            }
                          }
                        },
                        {
                          "match": {
                            "passengerEmail": {
                              "query": "?0",
                              "operator": "and",
                              "analyzer": "vi_folded"
                            }
                          }
                        }
                      ]
                    }
                  },
                  {
                    "term": {
                      "status": "?1"
                    }
                  }
                ]
              }
            }
            """)
    List<PassengerJourneyDocument> findByPassengerAndStatus(String passengerQuery, String status);
}
