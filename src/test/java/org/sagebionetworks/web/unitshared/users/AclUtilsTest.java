package org.sagebionetworks.web.unitshared.users;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.util.ModelConstants;
import org.sagebionetworks.web.shared.users.AclUtils;
import org.sagebionetworks.web.shared.users.PermissionLevel;

public class AclUtilsTest {

	@Test
	public void testGetPermissionLevel() {
		assertEquals(PermissionLevel.CAN_VIEW, AclUtils.getPermissionLevel(getReadAccessTypeSet()));
		assertEquals(PermissionLevel.CAN_SUBMIT_EVALUATION, AclUtils.getPermissionLevel(getParticipateAccessTypeSet()));
		assertEquals(PermissionLevel.CAN_SCORE_EVALUATION, AclUtils.getPermissionLevel(getScoreAccessTypeSet()));
		assertEquals(PermissionLevel.CAN_EDIT, AclUtils.getPermissionLevel(getEditAccessTypeSet()));
		assertEquals(PermissionLevel.CAN_EDIT_DELETE, AclUtils.getPermissionLevel(getEditDeleteAccessTypeSet()));
		assertEquals(PermissionLevel.CAN_ADMINISTER, AclUtils.getPermissionLevel(getAdminAccessTypeSet()));
		assertEquals(PermissionLevel.CAN_ADMINISTER_EVALUATION, AclUtils.getPermissionLevel(getEvalAdminAccessTypeSet()));
		assertEquals(PermissionLevel.CAN_MESSAGE_TEAM, AclUtils.getPermissionLevel(ModelConstants.TEAM_MESSENGER_PERMISSIONS));
		assertEquals(PermissionLevel.CAN_ADMINISTER_TEAM, AclUtils.getPermissionLevel(ModelConstants.TEAM_ADMIN_PERMISSIONS));
	}

	
	@Test
	public void testGetACCESS_TYPEs() {
		assertEquals(getReadAccessTypeSet(), AclUtils.getACCESS_TYPEs(PermissionLevel.CAN_VIEW));
		assertEquals(getParticipateAccessTypeSet(), AclUtils.getACCESS_TYPEs(PermissionLevel.CAN_SUBMIT_EVALUATION));
		assertEquals(getScoreAccessTypeSet(), AclUtils.getACCESS_TYPEs(PermissionLevel.CAN_SCORE_EVALUATION));
		assertEquals(getEditAccessTypeSet(), AclUtils.getACCESS_TYPEs(PermissionLevel.CAN_EDIT));
		assertEquals(getEditDeleteAccessTypeSet(), AclUtils.getACCESS_TYPEs(PermissionLevel.CAN_EDIT_DELETE));
		assertEquals(getAdminAccessTypeSet(), AclUtils.getACCESS_TYPEs(PermissionLevel.CAN_ADMINISTER));
		assertEquals(getEvalAdminAccessTypeSet(), AclUtils.getACCESS_TYPEs(PermissionLevel.CAN_ADMINISTER_EVALUATION));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPermissionLevels() {		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_VIEW, PermissionLevel.CAN_SUBMIT_EVALUATION, PermissionLevel.CAN_SCORE_EVALUATION, PermissionLevel.CAN_EDIT, 
				PermissionLevel.CAN_EDIT_DELETE, PermissionLevel.CAN_ADMINISTER, PermissionLevel.CAN_ADMINISTER_EVALUATION,
				PermissionLevel.CAN_MESSAGE_TEAM, PermissionLevel.CAN_ADMINISTER_TEAM})), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.READ));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_SUBMIT_EVALUATION, PermissionLevel.CAN_ADMINISTER_EVALUATION })), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.SUBMIT));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_EDIT, PermissionLevel.CAN_EDIT_DELETE, PermissionLevel.CAN_ADMINISTER, 
				PermissionLevel.CAN_ADMINISTER_EVALUATION, PermissionLevel.CAN_ADMINISTER_TEAM})), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.UPDATE));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_SCORE_EVALUATION, PermissionLevel.CAN_ADMINISTER_EVALUATION })), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.READ_PRIVATE_SUBMISSION));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_SCORE_EVALUATION, PermissionLevel.CAN_ADMINISTER_EVALUATION })), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.UPDATE_SUBMISSION));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_ADMINISTER_EVALUATION })), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.DELETE_SUBMISSION));

		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_EDIT, PermissionLevel.CAN_EDIT_DELETE, PermissionLevel.CAN_ADMINISTER, PermissionLevel.CAN_ADMINISTER_EVALUATION })), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.CREATE));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_EDIT_DELETE, PermissionLevel.CAN_ADMINISTER, PermissionLevel.CAN_ADMINISTER_EVALUATION,
				PermissionLevel.CAN_ADMINISTER_TEAM})), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.DELETE));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_ADMINISTER, PermissionLevel.CAN_ADMINISTER_EVALUATION })), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.CHANGE_PERMISSIONS));
		
		assertEquals(new HashSet<PermissionLevel>(Arrays.asList(new PermissionLevel[] { PermissionLevel.CAN_MESSAGE_TEAM, 
				PermissionLevel.CAN_ADMINISTER_TEAM})), 
				AclUtils.getPermisionLevels(ACCESS_TYPE.SEND_MESSAGE));
	}
		
	/*
	 * Private methods
	 */
	private Set<ACCESS_TYPE> getReadAccessTypeSet() {
		Set<ACCESS_TYPE> set = new HashSet<ACCESS_TYPE>();		
		set.add(ACCESS_TYPE.READ);
		return set;
	}

	private Set<ACCESS_TYPE> getParticipateAccessTypeSet() {
		Set<ACCESS_TYPE> set = new HashSet<ACCESS_TYPE>();		
		set.add(ACCESS_TYPE.READ);
		set.add(ACCESS_TYPE.SUBMIT);
		return set;
	}
	
	private Set<ACCESS_TYPE> getScoreAccessTypeSet() {
		Set<ACCESS_TYPE> set = new HashSet<ACCESS_TYPE>();		
		set.add(ACCESS_TYPE.READ);
		set.add(ACCESS_TYPE.READ_PRIVATE_SUBMISSION);
		set.add(ACCESS_TYPE.UPDATE_SUBMISSION);
		return set;
	}

	
	private Set<ACCESS_TYPE> getEditAccessTypeSet() {
		Set<ACCESS_TYPE> set = new HashSet<ACCESS_TYPE>();		
		set.add(ACCESS_TYPE.CREATE);
		set.add(ACCESS_TYPE.READ);
		set.add(ACCESS_TYPE.UPDATE);
		return set;
	}
	
	private Set<ACCESS_TYPE> getEditDeleteAccessTypeSet() {
		Set<ACCESS_TYPE> set = new HashSet<ACCESS_TYPE>();		
		set.add(ACCESS_TYPE.CREATE);
		set.add(ACCESS_TYPE.READ);
		set.add(ACCESS_TYPE.UPDATE);
		set.add(ACCESS_TYPE.DELETE);
		return set;
	}
	
	private Set<ACCESS_TYPE> getAdminAccessTypeSet() {
		Set<ACCESS_TYPE> set = new HashSet<ACCESS_TYPE>();		
		set.add(ACCESS_TYPE.CREATE);
		set.add(ACCESS_TYPE.READ);
		set.add(ACCESS_TYPE.UPDATE);
		set.add(ACCESS_TYPE.DELETE);
		set.add(ACCESS_TYPE.CHANGE_PERMISSIONS);
		set.add(ACCESS_TYPE.CHANGE_SETTINGS);
		set.add(ACCESS_TYPE.MODERATE);
		return set;
	}
	
	private Set<ACCESS_TYPE> getEvalAdminAccessTypeSet() {
		Set<ACCESS_TYPE> set = new HashSet<ACCESS_TYPE>();		
		set.add(ACCESS_TYPE.CREATE);
		set.add(ACCESS_TYPE.READ);
		set.add(ACCESS_TYPE.SUBMIT);
		set.add(ACCESS_TYPE.UPDATE);
		set.add(ACCESS_TYPE.READ_PRIVATE_SUBMISSION);
		set.add(ACCESS_TYPE.UPDATE_SUBMISSION);
		set.add(ACCESS_TYPE.DELETE_SUBMISSION);
		set.add(ACCESS_TYPE.DELETE);
		set.add(ACCESS_TYPE.CHANGE_PERMISSIONS);
		return set;
	}
}

