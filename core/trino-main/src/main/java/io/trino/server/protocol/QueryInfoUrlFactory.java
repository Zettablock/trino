/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.server.protocol;

import io.trino.server.ServerConfig;
import io.trino.spi.QueryId;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class QueryInfoUrlFactory
{
    private final Optional<String> queryInfoUrlTemplate;

    private final boolean useHttpsUrlInResponse;

    @Inject
    public QueryInfoUrlFactory(ServerConfig serverConfig)
    {
        this.queryInfoUrlTemplate = serverConfig.getQueryInfoUrlTemplate();
        this.useHttpsUrlInResponse = serverConfig.useHttpsUrlInResponse();

        // verify the template is a valid URL
        queryInfoUrlTemplate.ifPresent(template -> {
            try {
                new URI(template.replace("${QUERY_ID}", "query_id_value"));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid query info URL template: " + template, e);
            }
        });
    }

    public Optional<URI> getQueryInfoUrl(QueryId queryId)
    {
        Optional<URI> result = queryInfoUrlTemplate
                .map(template -> template.replace("${QUERY_ID}", queryId.toString()))
                .map(URI::create);
        if (useHttpsUrlInResponse && result.isPresent()) {
            result = Optional.of(UriBuilder.fromUri(result.get()).scheme("https").build());
        }
        return result;
    }

    public static URI getQueryInfoUri(Optional<URI> queryInfoUrl, QueryId queryId, UriInfo uriInfo, boolean useHttpsUrlInResponse)
    {
        UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
        if (useHttpsUrlInResponse) {
            uriBuilder = uriBuilder.scheme("https");
        }
        UriBuilder uriBuilderFinal = uriBuilder;
        return queryInfoUrl.orElseGet(() ->
                uriBuilderFinal.replacePath("ui/query.html")
                        .replaceQuery(queryId.toString())
                        .build());
    }
}
