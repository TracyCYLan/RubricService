package edu.csula.rubrics.models.dao;

import edu.csula.rubrics.models.External;

public interface ExternalDao {

	External getExternal(Long id);

	External saveExternal(External external);

	long checkExists(String extsource, String extid, String type);

}
