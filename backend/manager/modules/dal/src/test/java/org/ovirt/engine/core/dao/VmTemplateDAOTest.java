package org.ovirt.engine.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.core.common.businessentities.VmTemplateStatus;
import org.ovirt.engine.core.compat.Guid;

public class VmTemplateDAOTest extends BaseDAOTestCase {
    private static final Guid EXISTING_TEMPLATE_ID = new Guid("1b85420c-b84c-4f29-997e-0eb674b40b79");
    private static final Guid DELETABLE_TEMPLATE_ID = new Guid("1b85420c-b84c-4f29-997e-0eb674b40b80");
    private static final Guid STORAGE_DOMAIN_ID = new Guid("72e3a666-89e1-4005-a7ca-f7548004a9ab");
    private static final Guid VDS_GROUP_ID = new Guid("b399944a-81ab-4ec5-8266-e19ba7c3c9d1");

    private VmTemplateDAO dao;

    private VmTemplate newVmTemplate;
    private VmTemplate existingTemplate;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        dao = dbFacade.getVmTemplateDAO();

        existingTemplate = dao.get(
                new Guid("1b85420c-b84c-4f29-997e-0eb674b40b79"));

        newVmTemplate = new VmTemplate();
        newVmTemplate.setId(Guid.NewGuid());
        newVmTemplate.setname("NewVmTemplate");
        newVmTemplate.setvds_group_id(VDS_GROUP_ID);
    }

    /**
     * Ensures an id is required.
     */
    @Test
    public void testGetWithInvalidId() {
        VmTemplate result = dao.get(Guid.NewGuid());

        assertNull(result);
    }

    /**
     * Ensures that the right template is returned.
     */
    @Test
    public void testGet() {
        VmTemplate result = dao.get(EXISTING_TEMPLATE_ID);

        assertGetResult(result);
    }

    /**
     * Ensures that all templates are returned.
     */
    @Test
    public void testGetAll() {
        List<VmTemplate> result = dao.getAll();

        assertGetAllResult(result);
    }

    /**
     * Ensures the right set of templates are returned.
     */
    @Test
    public void testGetAllForStorageDomain() {
        List<VmTemplate> result = dao.getAllForStorageDomain(STORAGE_DOMAIN_ID);

        assertGetAllResult(result);
    }

    /**
     * Asserts that the right collection containing the vm templates is returned for a privileged user with filtering enabled
     */
    @Test
    public void testGetAllForStorageDomainWithPermissionsForPriviligedUser() {
        List<VmTemplate> result = dao.getAllForStorageDomain(STORAGE_DOMAIN_ID, PRIVILEGED_USER_ID, true);
        assertGetAllResult(result);
    }

    /**
     * Asserts that an empty collection is returned for a non privileged user with filtering enabled
     */
    @Test
    public void testGetAllForStorageDomainWithPermissionsForUnpriviligedUser() {
        List<VmTemplate> result = dao.getAllForStorageDomain(STORAGE_DOMAIN_ID, UNPRIVILEGED_USER_ID, true);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Asserts that the right collection containing the vm templates is returned for a non privileged user with filtering disabled
     */
    @Test
    public void testGetAllForStorageDomainWithPermissionsDisabledForUnpriviligedUser() {
        List<VmTemplate> result = dao.getAllForStorageDomain(STORAGE_DOMAIN_ID, UNPRIVILEGED_USER_ID, false);
        assertGetAllResult(result);
    }

    /**
     * Ensures the right set of templates are returned.
     */
    @Test
    public void testGetAllTemplatesRelatedToQuotaId() {
        List<VmTemplate> result = dao.getAllTemplatesRelatedToQuotaId(FixturesTool.QUOTA_GENERAL);

        assertGetAllResult(result);
    }

    /**
     * Ensures that the templates for the given vds group are returned.
     */
    @Test
    public void testGetAllForVdsGroup() {
        List<VmTemplate> result = dao.getAllForVdsGroup(VDS_GROUP_ID);

        assertGetAllResult(result);
        for (VmTemplate template : result) {
            assertEquals(VDS_GROUP_ID, template.getvds_group_id());
        }
    }

    /**
     * Ensures that the templates for the give image are returned.
     */
    @Test
    public void testGetAllForImage() {
        Map<Boolean, List<VmTemplate>> result = dao.getAllForImage(FixturesTool.TEMPLATE_IMAGE_ID);

        assertEquals("Wrong number of keys", 1, result.size());
        for (List<VmTemplate> templates : result.values()) {
            assertEquals("Wrong number of templates", 1, templates.size());
        }
    }

    /**
     * Ensures that saving a template works as expected.
     */
    @Test
    public void testSave() {
        dao.save(newVmTemplate);

        VmTemplate result = dbFacade.getVmTemplateDAO().get(newVmTemplate.getId());

        assertNotNull(result);
        assertEquals(newVmTemplate, result);
    }

    /**
     * Ensures that updating a template works as expected.
     */
    @Test
    public void testUpdate() {
        existingTemplate.setdescription("This is an updated description");

        dao.update(existingTemplate);

        VmTemplate result = dbFacade.getVmTemplateDAO().get(existingTemplate.getId());

        assertNotNull(result);
        assertEquals(existingTemplate, result);
    }

    /**
     * Ensures updating the status aspect of the VM Template works.
     */
    @Test
    public void testUpdateStatus() {
        VmTemplate before = dao.get(existingTemplate.getId());

        before.setstatus(VmTemplateStatus.Locked);
        dao.updateStatus(existingTemplate.getId(), VmTemplateStatus.Locked);

        VmTemplate after = dao.get(existingTemplate.getId());

        assertEquals(before, after);
    }

    /**
     * Ensures that removing a template works as expected.
     */
    @Test
    public void testRemove() {
        VmTemplate before = dbFacade.getVmTemplateDAO().get(DELETABLE_TEMPLATE_ID);

        assertNotNull(before);

        dao.remove(DELETABLE_TEMPLATE_ID);

        VmTemplate after = dbFacade.getVmTemplateDAO().get(DELETABLE_TEMPLATE_ID);

        assertNull(after);
    }

    /**
    * Asserts that the right template is returned for a privileged user with filtering enabled
    */
    @Test
    public void testGetWithPermissionsForPriviligedUser() {
        VmTemplate result = dao.get(EXISTING_TEMPLATE_ID, PRIVILEGED_USER_ID, true);
        assertGetResult(result);
    }

    /**
    * Asserts that no result is returned for a non privileged user with filtering enabled
    */
    @Test
    public void testGetWithPermissionsForUnpriviligedUser() {
        VmTemplate result = dao.get(EXISTING_TEMPLATE_ID, UNPRIVILEGED_USER_ID, true);
        assertNull(result);
    }

    /**
    * Asserts that the right template is returned for a non privileged user with filtering disabled
    */
    @Test
    public void testGetWithPermissionsDisabledForUnpriviligedUser() {
        VmTemplate result = dao.get(EXISTING_TEMPLATE_ID, UNPRIVILEGED_USER_ID, false);
        assertGetResult(result);
    }

    private void assertGetResult(VmTemplate result) {
        assertNotNull(result);
        assertEquals(EXISTING_TEMPLATE_ID, result.getId());
    }

    private void assertGetAllResult(List<VmTemplate> result) {
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
