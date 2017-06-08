package org.nuxeo.training.project;

import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;

/**
 * Tests Event Listener with Security
 *
 * @since 8.10
 */
@Features(PlatformFeature.class)
@Deploy({ "org.nuxeo.training.project.compute-core", "org.nuxeo.ecm.platform.collections.core",
        "studio.extensions.pabgrall-SANDBOX", "org.nuxeo.ecm.platform.types.core", "org.nuxeo.ecm.core.cache",
        "org.nuxeo.ecm.platform.usermanager.api", "org.nuxeo.ecm.platform.usermanager" })
public class TestFeature extends SimpleFeature {

}
