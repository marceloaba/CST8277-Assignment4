/**
 * File: BloodType.java
 * Course materials (21S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
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

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.Hibernate;

@Embeddable
public class BloodType {

	@Basic( optional = false)
	@Column( name = "blood_group", nullable = false)
	private String bloodGroup;

	@Basic( optional = false)
	@Column( name = "rhd", nullable = false)
	private byte rhd;

	public BloodType() {
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup( String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public void setType( String group, String rhd) {
		setBloodGroup( group);
		byte p = 0b1;
		byte n = 0b0;
		setRhd( ( "+".equals( rhd) ? p : n));
	}

	public byte getRhd() {
		return rhd;
	}

	public void setRhd( byte rhd) {
		this.rhd = rhd;
	}

	@Override
	public int hashCode() {
		return Objects.hash( getBloodGroup(), getRhd());
	}

	@Override
	public boolean equals( Object obj) {
		if ( obj == null)
			return false;
		if ( this == obj)
			return true;
		if ( !( getClass() == obj.getClass() || Hibernate.getClass( obj) == getClass()))
			return false;
		BloodType other = (BloodType) obj;
		return Objects.equals( getBloodGroup(), other.getBloodGroup()) && getRhd() == other.getRhd();
	}
}
