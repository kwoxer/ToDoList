package de.fhb.maus.android.todolist.contact;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = null;
	private String number = null;
	private String email = null;
	private boolean selected ;
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	private long contactid;

	
	public Contact (){
		
	}
	public long getContactid() {
		return contactid;
	}
	public void setContactid(long contactid) {
		this.contactid = contactid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(number);
		dest.writeString(email);
		dest.writeLong(contactid);
	}
	
	private Contact(Parcel in){
		name = in.readString();
		number = in.readString();
		email = in.readString();
		contactid = in.readLong();	
	}
	public static final Parcelable.Creator<Contact> CREATOR = 
			new Parcelable.Creator<Contact>() {
				@Override
				public Contact createFromParcel(Parcel in){
					return new Contact(in);
				}

				@Override
				public Contact[] newArray(int size) {
					// TODO Auto-generated method stub
					return new Contact[size];
				}
				};
				
}
