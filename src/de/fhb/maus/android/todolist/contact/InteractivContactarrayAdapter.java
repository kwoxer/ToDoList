package de.fhb.maus.android.todolist.contact;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.sending.SendingTextActivity;

@SuppressWarnings("rawtypes")
public class InteractivContactarrayAdapter extends ArrayAdapter {

	private final List<Contact> list;
	private final Activity context;
	private final String rowId;

	public InteractivContactarrayAdapter(Activity context, List<Contact> list,
			String rowId) {
		super(context, R.layout.contact_row, list);
		this.context = context;
		this.list = list;
		this.rowId = rowId;
	}

	/*
	 * ui elemts for the ViewHolder
	 */
	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
		protected Button sendEmail, sendSms;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		View view = null;
		if (convertview == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			view = inflater.inflate(R.layout.contact_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			// initialise the ui elemts for the viewholder
			viewHolder.text = (TextView) view
					.findViewById(R.id.textViewContactName);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Contact element = (Contact) viewHolder.checkbox
									.getTag();
							element.setSelected(buttonView.isChecked());
						}
					});
			viewHolder.checkbox.setTag(list.get(position));
			viewHolder.sendSms = (Button) view.findViewById(R.id.buttonsendSms);

			// set the visibility if a contact had a phonenumber
			if (list.get(position).getNumber() == null) {
				viewHolder.sendSms.setVisibility(android.view.View.INVISIBLE);
			} else {
				viewHolder.sendSms.setVisibility(android.view.View.VISIBLE);
			}
			viewHolder.sendSms.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Contact element = (Contact) viewHolder.sendSms.getTag();
					Intent intent = new Intent(getContext(),
							SendingTextActivity.class);
					intent.putExtra("contact", element);
					intent.putExtra("sms", true);
					intent.putExtra("rowId", rowId);
					getContext().startActivity(intent);
				}
			});

			viewHolder.sendEmail = (Button) view
					.findViewById(R.id.buttonSendEmail);
			// set the visibility if a contact had a emailadress
			if (list.get(position).getEmail() == null) {
				viewHolder.sendEmail.setVisibility(android.view.View.INVISIBLE);
			} else {
				viewHolder.sendEmail.setVisibility(android.view.View.VISIBLE);
			}
			viewHolder.sendEmail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Contact element = (Contact) viewHolder.sendSms.getTag();
					Intent intent = new Intent(getContext(),
							SendingTextActivity.class);
					intent.putExtra("contact", element);
					intent.putExtra("sms", false);
					intent.putExtra("rowId", rowId);
					getContext().startActivity(intent);

				}
			});

			// set the options for each list in the listview
			view.setTag(viewHolder);
			viewHolder.sendSms.setTag(list.get(position));
			viewHolder.sendEmail.setTag(list.get(position));

		} else {
			view = convertview;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).getName());
		holder.checkbox.setChecked(list.get(position).isSelected());

		return view;
	}
}
