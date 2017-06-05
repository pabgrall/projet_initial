package org.nuxeo.training.project;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.collections.core.adapter.Collection;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentSecurityException;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.NuxeoGroupImpl;
import org.nuxeo.ecm.core.api.impl.UserPrincipal;
import org.nuxeo.ecm.core.api.local.ClientLoginModule;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACE.ACEBuilder;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventListenerDescriptor;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.login.test.ClientLoginFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@Deploy({ "org.nuxeo.training.project.ComputePrice-core", "org.nuxeo.ecm.platform.collections.core",
        "studio.extensions.pabgrall-SANDBOX",
        /*
         * "org.nuxeo.ecm.automation.core", "org.nuxeo.ecm.automation.features", "org.nuxeo.ecm.platform.query.api",
         * "org.nuxeo.runtime.management",
         */
        "org.nuxeo.ecm.platform.types.core", "org.nuxeo.ecm.core.cache",
        /*
         * "org.nuxeo.ecm.platform.api", "org.nuxeo.ecm.platform.content.template", "org.nuxeo.ecm.platform.dublincore",
         * "org.nuxeo.ecm.platform.usermanager.api", "org.nuxeo.ecm.platform.usermanager", "org.nuxeo.ecm.core.io",
         * "org.nuxeo.ecm.platform.query.api", "org.nuxeo.ecm.platform.test:test-usermanagerimpl/directory-config.xml"
         */
        "org.nuxeo.ecm.platform.usermanager.api", "org.nuxeo.ecm.platform.usermanager", })

public class TestProductNotSold {

    protected final List<String> events = Arrays.asList("productnotsold", "documentModified");

    private static final Log log = LogFactory.getLog(TestProductNotSold.class);

    @Inject
    protected EventService s;

    @Inject
    CoreSession session;

    @Inject
    protected ClientLoginFeature login;

    @Inject
    protected UserManager userManager;

    private final String HiddenFolderXPath = "/default-domain/hidden";

    private final String doctype = "Products";

    private final String picture = "Visual";

    @Test
    public void listenerRegistration() {

        log.debug("Before Folder creation");

        // Creation of default domain root

        DocumentModel doc1 = session.createDocumentModel("/", "default-domain", "Folder");
        doc1 = session.createDocument(doc1);
        session.saveDocument(doc1);

        // Creation of directory hidden from users of Group1

        doc1 = session.createDocumentModel("/default-domain", "hidden", "Folder");
        doc1 = session.createDocument(doc1);
        session.saveDocument(doc1);

        // creation of user1, part of Group1

        DocumentModel group1 = userManager.getBareGroupModel();
        String schemaGroup = userManager.getGroupSchemaName();
        group1.setProperty(schemaGroup, "groupname", "group1");
        group1.setProperty(schemaGroup, "grouplabel", "Group1");
        userManager.createGroup(group1);

        DocumentModel user1 = userManager.getBareUserModel();
        String schemaUser = userManager.getUserSchemaName();
        user1.setProperty(schemaUser, "username", "user1");
        userManager.createUser(user1);

        NuxeoGroup group = userManager.getGroup("group1");
        List<String> listName = new ArrayList<String>();
        listName.add(group.getName());
        group.setMemberGroups(listName);

        // creation de l'arborescence de repertoires

        DocumentModel docw = session.createDocumentModel("/default-domain", "Workspaces", "Folder");
        docw = session.createDocument(docw);
        session.saveDocument(docw);

        DocumentModel docf = session.createDocumentModel("/default-domain/Workspaces",
                "Start Creating Your Content Here", "Workspace");
        docf = session.createDocument(docf);
        session.saveDocument(docf);

        // Product creation

        log.debug("Before Products creation");

        DocumentModel doc2 = session.createDocumentModel("/default-domain/Workspaces/Start Creating Your Content Here",
                "test-notsold", doctype);
        doc2 = session.createDocument(doc2);
        ProductsAdapter adapter = doc2.getAdapter(ProductsAdapter.class);
        adapter.setPrice("50.0");
        adapter.setTitle("Inserted doc");
        adapter.setSellState(true);
        session.saveDocument(doc2);

        // SETTING ACE on default folder for user user1 , MANAGE, READ, WRITE
        ACEBuilder aceb0 = ACE.builder("user1", "");
        ACEBuilder aceb1 = ACE.builder("user1", "Manage");
        ACEBuilder aceb2 = ACE.builder("user1", "Write");
        ACEBuilder aceb3 = ACE.builder("user1", "Read");
        ACEBuilder aceb4 = ACE.builder("user1", "AddChildren"); // NOT EFFECTIVE FOR USER1
        ACE ace0 = aceb0.build();
        ACE ace1 = aceb1.build();
        ACE ace2 = aceb2.build();
        ACE ace3 = aceb3.build();
        ACE ace4 = aceb4.build();
        ACLImpl acl = new ACLImpl();
        acl.add(ace1);
        acl.add(ace2);
        acl.add(ace3);
        acl.add(ace4);
        ACPImpl acp = new ACPImpl();
        acp.addACL(acl);
        doc2.setACP(acp, true);
        // ADD AddChildren Privs to user1 on Collection
        /*
         * server.close(client); // NO MORE USER1 acl = new ACLImpl(); acl.add(ace4); acp = new ACPImpl();
         * acp.addACL(acl); doc2.setACP(acp, false); // client = CoreInstance.openCoreSession(repoName, np); // USER1
         * AGAIN
         */

        // HERE, only READ permission for USER1
        acl = new ACLImpl();
        acl.add(ace3);
        acp = new ACPImpl();
        acp.addACL(acl);
        docf.setACP(acp, true);
        docw.setACP(acp, true);

        DocumentModel docRoot = session.createDocumentModel("/", "", "Folder");
        docRoot = session.getDocument(docRoot.getRef());
        docRoot.setACP(acp, true);

        // ACL for no access on hidden folder for user1
        acl = new ACLImpl();
        acp = new ACPImpl();
        acp.addACL(acl);
        acp.blockInheritance("Read", "user1");
        acp.blockInheritance("Everything", "user1");
        acp.removeACEsByUsername("Read", "user1");
        acp.removeACEsByUsername("Everything", "user1");
        doc1.setACP(acp, true);

        // impersonate user1
        //
        // FROM HERE ON, WE ARE USER1

        CoreInstance server = CoreInstance.getInstance();
        UserPrincipal np = new UserPrincipal("user1");

        RepositoryManager repositoryManager = Framework.getLocalService(RepositoryManager.class);
        String repoName = repositoryManager.getDefaultRepositoryName();

        CoreSession client = CoreInstance.openCoreSession(repoName, np);

        ACP a = doc1.getACP();
        for (ACL b : a.getACLs()) {
            log.debug(b.getName());
            for (ACE c : b) {
                log.debug(c.toString());
            }
        }

        DocumentModel doc3 = client.createDocumentModel("/default-domain/Workspaces/Start Creating Your Content Here",
                "test-notsold", doctype);
        // ... do something in that session ...

        try {
            doc3 = client.getDocument(doc3.getRef());
        } catch (DocumentNotFoundException dnfe) {
            doc3 = null;
        }

        assertNotNull(doc3);
        assertTrue(client.hasPermission(doc3.getRef(), "Read"));

        /*
         * DocumentModel doc3 = session.createDocumentModel("/default-domain/hidden", "test-notsold", doctype);
         * assertNotNull(doc3); try { doc3 = session.getDocument(doc3.getRef()); } catch (DocumentNotFoundException
         * dnfe) { doc3 = null; } assertNull(doc3);
         */
        log.debug("before adding Visuals");

        server.close(client); // NO MORE USER1

        // See
        // https://answers.nuxeo.com/general/q/9c1064d4a96840a48c10552144d19a15/Problem-whith-Privilege-AddChildren-is-not-granted
        // Only Administrator have AddChildren permission
        DocumentModel docv1 = session.createDocumentModel("/default-domain/Workspaces/Start Creating Your Content Here",
                "mypicture1", picture);
        docv1 = session.createDocument(docv1);
        session.saveDocument(docv1);
        DocumentModel docv2 = session.createDocumentModel("/default-domain/Workspaces/Start Creating Your Content Here",
                "mypicture2", picture);
        docv2 = session.createDocument(docv2);
        session.saveDocument(docv2);
        CollectionManager cm = Framework.getLocalService(CollectionManager.class);
        assertNotNull(cm);
        cm.addToCollection(doc2, docv1, client);
        cm.addToCollection(doc2, docv2, client);
        session.saveDocument(doc2);

        log.debug(picture + "s added");

        log.debug("Before checking listener");

        EventListenerDescriptor listener = s.getEventListener("productnotsold");
        assertNotNull(listener);
        assertTrue(events.stream().allMatch(listener::acceptEvent));

        log.debug("Before modifying doc");

        adapter.setSellState(false);
        session.saveDocument(doc2);

        client = CoreInstance.openCoreSession(repoName, np);
        // WE ARE USER1 AGAIN
        /*
         * log.debug("Before triggering event"); EventProducer eventProducer =
         * Framework.getService(EventProducer.class); DocumentEventContext ctx = new DocumentEventContext(session,
         * session.getPrincipal(), doc2); Event event = ctx.newEvent("productnotsold"); eventProducer.fireEvent(event);
         */
        /*
         * the following is a workaround against the fact that the event can be triggered in another thread and a window
         * of concurrence exists with current thread... Happy to learn about other implementations
         */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }

        // Before checking action of event listener
        // 2 Visuals should now he hidden
        log.debug("Before checking document move");

        DocumentModel doc4 = client.createDocumentModel("/default-domain/hidden", "mypicture1", picture);
        try {
            doc4 = client.getDocument(doc4.getRef());
        } catch (DocumentNotFoundException dnfe) {
            doc4 = null;
        } catch (DocumentSecurityException dse) {
            // we should pass here
            doc4 = null;
        }
        assertNull(doc4);
        // assertEquals(doc4.getPathAsString(), HiddenFolderXPath + "/mypicture1");

        DocumentModel doc5 = client.createDocumentModel("/default-domain/hidden", "mypicture2", picture);
        try {
            doc5 = client.getDocument(doc5.getRef());
        } catch (DocumentNotFoundException dnfe) {
            doc5 = null;
            assertFalse(true);
        } catch (DocumentSecurityException dse) {
            // we should pass here
            doc5 = null;
        }
        assertNull(doc5);
        // assertEquals(doc5.getPathAsString(), HiddenFolderXPath + "/mypicture2");

        // close the client -> this is closing the core session
        server.close(client);
        // FROM HERE ON, WE ARE AGAIN ADMINISTRATOR or MAIN TESTER

        // checking the collection size of the Product...

        doc3 = session.createDocumentModel("/default-domain/Workspaces/Start Creating Your Content Here",
                "test-notsold", doctype);
        assertNotNull(doc3);
        try {
            doc3 = session.getDocument(doc3.getRef());
        } catch (DocumentNotFoundException dnfe) {
            doc3 = null;
        }

        assertNotNull(doc3); // Product still exists

        Collection colladapter = doc3.getAdapter(Collection.class);
        List<String> ids = colladapter.getCollectedDocumentIds();
        assertEquals(ids.size(), 2); // Collection size is STILL 2

        /*
         * try { login.login("UserFromGroupEveryone"); } catch (LoginException le) {
         * log.warn("Cannot impersonate a user for group Everyone"); } assertEquals("UserFromGroupEveryone",
         * ClientLoginModule.getCurrentPrincipal().toString());
         */

        /*
         * http://community.nuxeo.com/api/nuxeo/5.5/javadoc/org/nuxeo/ecm/core/api/CoreInstance.html CoreInstance server
         * = CoreInstance.getInstance(); CoreSession client = server.open("demo", null); DocumentModel root =
         * client.getRootDocument(); // ... do something in that session ... // close the client -> this is closing the
         * core session server.close(client);
         */
        
        // REMINDER, WE ARE NOW AGAIN ADMINISTRATOR or MAIN TESTER in session
        doc5 = session.createDocumentModel("/default-domain/hidden", "mypicture2", picture);

        // ... do something in that session ...

        ACP z = doc5.getACP();
        for (ACL y : z.getACLs()) {
            log.debug(y.getName());
            for (ACE x : y) {
                log.debug(x.toString());
            }
        }

        log.debug(session.getPrincipal().getName());

        assertTrue(session.hasPermission(doc5.getRef(), "Read"));

        try {
            doc5 = session.getDocument(doc5.getRef());
        } catch (DocumentNotFoundException dnfe) {
            // we should pass here
            doc5 = null;
        } catch (DocumentSecurityException dse) {
            doc5 = null;
            assertFalse(true);
        }

        assertNotNull(doc5);
    }
}
