package org.exoplatform.support.cleaner;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;

/**
 * Created by exo on 7/13/16.
 */
@Path("/cleaner")
public class SocialCleanerRest implements ResourceContainer {

    private SocialCleanerService socialCleanerService =
            (SocialCleanerService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SocialCleanerService.class);

    @GET
    @Path("/spaces")
    public String cleanSpaces() throws RepositoryException, RepositoryConfigurationException, IOException {
        return socialCleanerService.updateSpacesNodes();

    }

}
