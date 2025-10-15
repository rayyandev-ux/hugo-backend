package com.vocacional.prestamoinso.Config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String anonKey;

    @Value("${supabase.service.role.key}")
    private String serviceRoleKey;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    public String getAnonKey() {
        return anonKey;
    }

    public String getServiceRoleKey() {
        return serviceRoleKey;
    }

    public String getRestApiUrl() {
        return supabaseUrl + "/rest/v1";
    }
}