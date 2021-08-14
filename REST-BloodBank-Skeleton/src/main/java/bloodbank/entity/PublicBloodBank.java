/***************************************************************************
 * File: PublicBloodBank.java Course materials (21F) CST 8277
 * 
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * 
 * @author (Original) Mike Norman
 * @date 2020 04
 * 
 * Updated by Students:
 * 	@author Chrishanthi Michael
 * 	@author Marcelo Monteiro da Silva
 * 	@author Janio Mendonca Junior
 * 	@author Parnoor Singh Gill
 * 
 * @date 13/08/2021
 */
package bloodbank.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue( "0")
public class PublicBloodBank extends BloodBank {

	private static final long serialVersionUID = 1L;

	public PublicBloodBank() {
		super(true);
	}
}
