package digipost.gradle

import digipost.gradle.model.*

public class ResultManipulationUtil{
	private void updateDigipostCustomers(ArrayList candidates, Map resultat) {
			
			if(candidates.get(0) instanceof Person){
				for(Person p in candidates){
					if(resultat.get(p.kunde_id) != null){
						p.resultat = resultat.get(p.kunde_id);
						
					}
					else {
						p.resultat = 'N/A';
						
					}
				}
			}
			else if(candidates.get(0) instanceof Organization){
				for(Organization o in candidates){
					if(resultat.get(o.kunde_id) != null){
						o.resultat = resultat.get(o.kunde_id);
						
					}
					else {
						o.resultat = 'N/A';
						
					}
				}
			}
	}
}