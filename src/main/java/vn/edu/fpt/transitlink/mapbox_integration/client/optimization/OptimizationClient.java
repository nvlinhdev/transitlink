package vn.edu.fpt.transitlink.mapbox_integration.client.optimization;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.request.OptimizationRequestBody;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response.RetrieveResponse;
import vn.edu.fpt.transitlink.mapbox_integration.client.optimization.dto.response.SubmissionResponse;

@HttpExchange(url = "/optimized-trips/v2", accept = "application/json")
public interface OptimizationClient {
    @PostExchange
    ResponseEntity<SubmissionResponse> submitRoutingProblem(@RequestBody OptimizationRequestBody optimizationRequestBody);

    @GetExchange(url = "/{jobId}")
    ResponseEntity<RetrieveResponse> retrieveSolution(@PathVariable String jobId);
}
