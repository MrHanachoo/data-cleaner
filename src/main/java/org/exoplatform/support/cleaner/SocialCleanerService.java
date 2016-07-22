package org.exoplatform.support.cleaner;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.impl.core.value.StringValue;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.*;
import java.io.IOException;
import java.util.*;

/**
 * Created by exo on 7/13/16.
 */
public class SocialCleanerService {

    private static final Log LOG = ExoLogger.getLogger(SocialCleanerService.class);
    private ValueFactory valueFactory;

    /**
     *  JCR session handler
     */
    private Session getSession() throws RepositoryConfigurationException, RepositoryException {
        RepositoryService repoService =
                (RepositoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RepositoryService.class);
        ManageableRepository repo = repoService.getRepository("repository");
        Session session = repo.getSystemSession("social");
        return session;
    }

    private String[] removeDuplicates(String[] strings) {
        List<String> list = Arrays.asList(strings);
        Set<String> set = new HashSet<String>(list);
        String[] strings1 = new String[set.size()];
        return set.toArray(strings1);
    }

    private void customLog(String s, String s1, Value[] values) throws RepositoryException {
        StringBuilder stringBuilder = new StringBuilder();
        //log the new properties values
        for (int i = 0; i < values.length; i++) {
            stringBuilder.append(values[i].getString()).append(", ");
        }
        LOG.info(s + "> " + s1 + "> " + stringBuilder);
    }

    /**
     *  lowerCase all users Ids in the spaces properties
     */
    public String updateSpacesNodes() throws RepositoryException, RepositoryConfigurationException, IOException {

        Session session = getSession();
        Node root = session.getRootNode();
        if (root.hasNode("production/soc:spaces/")) {
            NodeIterator nodeIterator = root.getNode("production/soc:spaces/").getNodes();
            while (nodeIterator.hasNext()) {
                Node spaceNode = nodeIterator.nextNode();
                PropertyIterator currentNodeProperties = spaceNode.getProperties();
                while (currentNodeProperties.hasNext()) {
                    Property property = currentNodeProperties.nextProperty();
                    if (property.getName().equals("soc:pendingMembersId") ||
                            property.getName().equals("soc:managerMembersId") ||
                            property.getName().equals("soc:invitedMembersId") ||
                            property.getName().equals("soc:membersId")) {

                        Iterator<Value> iterator = Arrays.asList(property.getValues()).iterator();

                        int i = 0;
                        String[] strings = new String[Arrays.asList(property.getValues()).size()];
                        while (iterator.hasNext()) {
                            strings[i] = iterator.next().getString().toLowerCase();
                            i++;
                        }
                        // Collections.sort(Arrays.asList(strings), String.CASE_INSENSITIVE_ORDER);
                        //Arrays.sort(strings);
                        String[] strings1 = removeDuplicates(strings);

                        //Value[] values = new Value[Arrays.asList(property.getValues()).size()];
                        Value[] values = new Value[strings1.length];
                        for (int k = 0; k < strings1.length; k++){
                            values[k] = new StringValue(strings1[k]);
                        }

                        spaceNode.setProperty(property.getName(), values);
                        customLog(spaceNode.getName(), property.getName(), values);
                    }
                }
                spaceNode.save();
            }
            session.save();
            return "spaces cleaned !!";
        }
        return null;
    }
}
