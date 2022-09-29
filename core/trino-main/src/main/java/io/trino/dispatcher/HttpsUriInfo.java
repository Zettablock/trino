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

package io.trino.dispatcher;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class HttpsUriInfo implements UriInfo {
    private final UriInfo originalUriInfo;

    public HttpsUriInfo(UriInfo originalUriInfo) {
        this.originalUriInfo = originalUriInfo;
    }

    @Override
    public String getPath() {
        return originalUriInfo.getPath();
    }

    @Override
    public String getPath(boolean decode) {
        return originalUriInfo.getPath(decode);
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return originalUriInfo.getPathSegments();
    }

    @Override
    public List<PathSegment> getPathSegments(boolean decode) {
        return originalUriInfo.getPathSegments(decode);
    }

    @Override
    public URI getRequestUri() {
        URI result = originalUriInfo.getRequestUri();
        if (result == null) {
            return null;
        }
        return UriBuilder.fromUri(result).scheme("https").build();
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        UriBuilder result = originalUriInfo.getRequestUriBuilder();
        if (result == null) {
            return null;
        }
        return result.scheme("https");
    }

    @Override
    public URI getAbsolutePath() {
        URI result = originalUriInfo.getAbsolutePath();
        if (result == null) {
            return null;
        }
        return UriBuilder.fromUri(result).scheme("https").build();
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        UriBuilder result = originalUriInfo.getAbsolutePathBuilder();
        if (result == null) {
            return null;
        }
        return result.scheme("https");
    }

    @Override
    public URI getBaseUri() {
        URI result = originalUriInfo.getBaseUri();
        if (result == null) {
            return null;
        }
        return UriBuilder.fromUri(result).scheme("https").build();
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        UriBuilder result = originalUriInfo.getBaseUriBuilder();
        if (result == null) {
            return null;
        }
        return result.scheme("https");
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        return originalUriInfo.getPathParameters();
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean decode) {
        return originalUriInfo.getPathParameters(decode);
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return originalUriInfo.getQueryParameters();
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        return originalUriInfo.getQueryParameters(decode);
    }

    @Override
    public List<String> getMatchedURIs() {
        return originalUriInfo.getMatchedURIs();
    }

    @Override
    public List<String> getMatchedURIs(boolean decode) {
        return originalUriInfo.getMatchedURIs(decode);
    }

    @Override
    public List<Object> getMatchedResources() {
        return originalUriInfo.getMatchedResources();
    }

    @Override
    public URI resolve(URI uri) {
        URI result = originalUriInfo.resolve(uri);
        if (result == null) {
            return null;
        }
        return UriBuilder.fromUri(result).scheme("https").build();
    }

    @Override
    public URI relativize(URI uri) {
        URI result = originalUriInfo.relativize(uri);
        if (result == null) {
            return null;
        }
        return UriBuilder.fromUri(result).scheme("https").build();
    }
}
