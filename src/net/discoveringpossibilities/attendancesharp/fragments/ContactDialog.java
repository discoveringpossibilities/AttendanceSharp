package net.discoveringpossibilities.attendancesharp.fragments;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import net.discoveringpossibilities.attendancesharp.R;

public class ContactDialog extends DialogFragment {

	@SuppressLint("InflateParams")
	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder contactDialog = new AlertDialog.Builder(getActivity());

		LayoutInflater mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = mLayoutInflater.inflate(R.layout.fragment_contact, null, false);
		
		CardView saksham_nanda_card = (CardView) mView.findViewById(R.id.saksham_nanda_card);
		CardView rajat_dhamija_card = (CardView) mView.findViewById(R.id.rajat_dhamija_card);
		CardView pramod_garg_card = (CardView) mView.findViewById(R.id.pramod_garg_card);

		ImageView fb_image_view = (ImageView) mView.findViewById(R.id.fb_image_view);
		ImageView tw_image_view = (ImageView) mView.findViewById(R.id.tw_image_view);
		ImageView insta_image_view = (ImageView) mView.findViewById(R.id.insta_image_view);
		ImageView gplus_image_view = (ImageView) mView.findViewById(R.id.gplus_image_view);
		ImageView yt_image_view = (ImageView) mView.findViewById(R.id.yt_image_view);
		ImageView web_image_view = (ImageView) mView.findViewById(R.id.web_image_view);

		saksham_nanda_card.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Builder CallDialogBuilder = new AlertDialog.Builder(getActivity());
				CallDialogBuilder.setNegativeButton("NO", null);
				CallDialogBuilder.setMessage("Call Saksham Nanda?");
				CallDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int) {
						try {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:+917696556566"));
							startActivity(callIntent);
						} catch (ActivityNotFoundException activityException) {
							Log.e("Calling: Saksham Nanda", "Call failed", activityException);
						}
					}
				});
				CallDialogBuilder.create();
				CallDialogBuilder.show();
			}
		});
		
		rajat_dhamija_card.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Builder CallDialogBuilder = new AlertDialog.Builder(getActivity());
				CallDialogBuilder.setNegativeButton("NO", null);
				CallDialogBuilder.setMessage("Call Rajat Dhamija?");
				CallDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int) {
						try {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:+919872226052"));
							startActivity(callIntent);
						} catch (ActivityNotFoundException activityException) {
							Log.e("Calling: Rajat Dhamija", "Call failed", activityException);
						}
					}
				});
				CallDialogBuilder.create();
				CallDialogBuilder.show();
			}
		});
		
		pramod_garg_card.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Builder CallDialogBuilder = new AlertDialog.Builder(getActivity());
				CallDialogBuilder.setNegativeButton("NO", null);
				CallDialogBuilder.setMessage("Call Pramod Garg?");
				CallDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int) {
						try {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:+7814488717"));
							startActivity(callIntent);
						} catch (ActivityNotFoundException activityException) {
							Log.e("Calling: Pramod Garg", "Call failed", activityException);
						}
					}
				});
				CallDialogBuilder.create();
				CallDialogBuilder.show();
			}
		});
		
		fb_image_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.facebook.com/")));
			}
		});
		tw_image_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://twitter.com/")));
			}
		});
		insta_image_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://instagram.com/")));
			}
		});
		gplus_image_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://plus.google.com/")));
			}
		});
		yt_image_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.youtube.com/")));
			}
		});
		web_image_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.discoveringpossibilities.net/")));
			}
		});
		contactDialog.setView(mView);
		return contactDialog.create();
	}
}
