package dev.gladikov.gateway.rest.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscoveryService {
    private final DiscoveryClient discoveryClient;

    public List<ServiceInstance> getInstances(String serviceName){
        return discoveryClient.getInstances (serviceName);
    }

    public String getServiceAddress(String serviceName){
        return discoveryClient.getInstances (serviceName).get (0).getUri ().toString ();
    }
}
