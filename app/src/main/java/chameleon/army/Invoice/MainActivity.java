package chameleon.army.Invoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/** Η μια και μοναδική (κύρια) "δραστηριότητα" του προγράμματος. */
public class MainActivity extends Activity {
	/** Λίστα με τις τιμές του επιλογέα ΦΕ. */
	static private final Object[] INCOME_TAX_DATA = { 4, 8, 3, 1, 10, 0 };
	/** Λίστα με τις τιμές του επιλογέα ΦΠΑ. */
	static private final Object[] VAT_DATA = { 24, 13, 6,    17, 9, 5,    13,    0 };

	/** Λίστα με όλες τις κρατήσεις στο Στρατό. */
	//                             ΜΤΣ,     Χαρτόσημο,ΟΓΑ,       ΕΑΑΔΗΣΥ,ΑΕΠΠ,   ΒΑΜ,  ΕΚΟΕΜΣ
	final static private Hold[] holdListArmy = {
	/*0*/	new Hold(new double[0]),															// 0 - Λογαριασμοί νερού, έργα ΔΕΗ
	/*1*/	new Hold(new double[] {0.03904, 0.0008,   0.00016}),								// 4 - Προμήθεια από Πρατήριο ή ΝΠΔΔ
	/*2*/	new Hold(new double[] {0.04,    0.0008,   0.00016}),								// 4.096 - Μισθώματα ακινήτων
	// ΤΑΚΤΙΚΟΣ Π/Υ
	// Καθαρή αξία < 1000
	/*3*/	new Hold(new double[] {0.04,    0.000818, 0.0001636, 0,      0.0006}), 				// 4.15816
	/*4*/	new Hold(new double[] {0.04,    0.002818, 0.0001636, 0,      0.0006}), 				// 4.35816 - Αμοιβές μελετητών
	// Καθαρή αξία >= 1000
	/*5*/	new Hold(new double[] {0.04,    0.000839, 0.0001678, 0.0007, 0.0006}), 				// 4.23068
	/*6*/	new Hold(new double[] {0.04,    0.002839, 0.0001678, 0.0007, 0.0006}), 				// 4.43068 - Αμοιβές μελετητών
	// Π/Υ ΠΔΕ
	// Καθαρή αξία < 1000
	/*7*/	new Hold(new double[] {0,       0.000018, 0.0000036, 0,      0.0006}),				// 0.06216
	/*8*/	new Hold(new double[] {0,       0.002018, 0.0000036, 0,      0.0006}),				// 0.26216 - Αμοιβές μελετητών
	// Καθαρή αξία >= 1000
	/*9*/	new Hold(new double[] {0,       0.000039, 0.0000078, 0.0007, 0.0006}),				// 0.13468
	/*10*/	new Hold(new double[] {0,       0.002039, 0.0000078, 0.0007, 0.0006}),				// 0.33468 - Αμοιβές μελετητών
	// ΙΔΙΟΙ ΠΟΡΟΙ
	/*11*/	new Hold(new double[] {0,       0,        0,         0,      0,      0.02, 0.08}),	// 10 - Λέσχες
	/*12*/	new Hold(new double[] {0.03904, 0.0008,   0.00016,   0,      0,      0.02, 0.08}),	// 14 - Προμήθεια από Πρατήριο ή ΝΠΔΔ
	/*13*/	new Hold(new double[] {0.04,	0.0008,   0.00016,   0,	     0,      0.02, 0.08}),	// 14.096 - Μισθώματα ακινήτων
	// Καθαρή αξία < 1000
	/*14*/	new Hold(new double[] {0.04,    0.000818, 0.0001636, 0,      0.0006, 0.02, 0.08}),	// 14.15816
	/*15*/	new Hold(new double[] {0.04,    0.002818, 0.0001636, 0,      0.0006, 0.02, 0.08}),	// 14.35816 - Αμοιβές μελετητών
	// Καθαρή αξία >= 1000
	/*16*/	new Hold(new double[] {0.04,	0.000839, 0.0001678, 0.0007, 0.0006, 0.02, 0.08}),	// 14.23068
	/*17*/	new Hold(new double[] {0.04,    0.002839, 0.0001678, 0.0007, 0.0006, 0.02, 0.08}),	// 14.43068 - Αμοιβές μελετητών
	};

	/** Λίστα με όλες τις κρατήσεις στην Αεροπορία. */
	//                             ΜΤΑ, ΕΛΟΑΑ Χαρτόσημο,ΟΓΑ,       ΕΑΑΔΗΣΥ,ΑΕΠΠ
	final static private Hold[] holdListAirForce = {
	/*0*/	holdListArmy[0],			        										// 0 - Λογαριασμοί νερού, έργα ΔΕΗ
	/*1*/	new Hold(new double[] {0.04, 0.02, 0.001218, 0.0002436, 0,      0.0006}),	// 6.20616 - Καθαρή αξία < 1000
	/*2*/	new Hold(new double[] {0.04, 0.02, 0.001239, 0.0002478, 0.0007, 0.0006}),	// 6.27868 - Καθαρή αξία >= 1000
	// Ανάγκες τροφοδοσίας και ατομικής καθαριότητας μαθητών/οπλιτών
	/*3*/	new Hold(new double[] {0,    0,    0.000018, 0.0000036, 0,      0.0006}),	// 0.06216 - Καθαρή αξία < 1000
	/*4*/	new Hold(new double[] {0,    0,    0.000039, 0.0000078, 0.0007, 0.0006}),	// 0.13468 - Καθαρή αξία >= 1000
	};

	/** Κλειδί αποθήκευσης του είδους του προμηθευτή. */
	static private final String CONTRACTOR = "Προμηθευτής";
	/** Κλειδί αποθήκευσης του ποσοστού ΦΠΑ. */
	static private final String VAT = "ΦΠΑ";
	/** Κλειδί αποθήκευσης του ποσοστού ΦΕ. */
	static private final String INCOME_TAX = "ΦΕ";
	/** Κλειδί αποθήκευσης του ποσοστού Κρατήσεων. */
	static private final String HOLDS = "Κρατήσεις";
	/** Κλειδί αποθήκευσης του τύπου του τιμολογίου. */
	static private final String INVOICE_TYPE = "ΤύποςΤιμολογίου";
	/** Κλειδί αποθήκευσης του τύπου του ποσού. */
	static private final String AMOUNT_TYPE = "ΤύποςΠοσού";
	/** Κλειδί αποθήκευσης του ποσού. */
	static private final String AMOUNT = "Ποσό";
	/** Κλειδί αποθήκευσης για το αν είναι ενεργός ο αυτόματος υπολογισμός κρατήσεων και ΦΕ. */
	static private final String AUTOMATIC = "Αυτόματο";
	/** Κλειδί αποθήκευσης για τον τύπο της χρηματοδότησης. */
	static private final String FINANCING_TYPE = "Χρηματοδότηση";
	/** Κλειδί αποθήκευσης για το αν η δαπάνη αφορά έργο. */
	static private final String CONSTRUCTION = "Εγκαταστάσεις";
	/** Κλειδί αποθήκευσης για τον κλάδο των Ενόπλων Δυνάμεων. */
	static private final String ARM_TYPE = "Κλάδος";

	/** Ο χρήστης είναι της Πολεμικής Αεροπορίς. */
	private boolean airforce;

	// Αρχικοποίηση του Activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		// Κοινός listener για spinner Κρατήσεων, ΦΠΑ και ΦΕ
		AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) {
				final int other = parentView.getCount() - 1;
				if (other == position) {    // Η τελευταία επιλογή είναι "Άλλο"
					// Ένας EditText για να εισάγουμε το νέο ποσοστό κρατήσεων ή ΦΠΑ ή ΦΕ
					final EditText txtNum = new EditText(MainActivity.this);
					final boolean holds = parentView == findViewById(R.id.spHolds);
					int flags = InputType.TYPE_CLASS_NUMBER;
					if (holds) flags |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
					txtNum.setInputType(flags);
					txtNum.setHint("%");

					new AlertDialog.Builder(MainActivity.this)
							.setTitle(R.string.newValue)
							.setView(txtNum)
							.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
								try {
									// Μετατροπή της εισόδου σε αριθμό στο [0, 80]
									String s = txtNum.getText().toString();
									Number num = holds ? Double.parseDouble(s) : Integer.parseInt(s);
									if (num.intValue() > 80) {    // Η είσοδος δεν είναι έγκυρος αριθμός
										parentView.setSelection(0);
										Toast.makeText(MainActivity.this, R.string.invalidValue, Toast.LENGTH_SHORT).show();
										return;
									}
									// Αν ο αριθμός ήδη υπάρχει στη λίστα...
									for (int z = 0; z < other; ++z)
										if (parentView.getItemAtPosition(z).equals(num)) {
											parentView.setSelection(z);	// ...τον επιλέγει
											Toast.makeText(MainActivity.this, R.string.existedValue, Toast.LENGTH_SHORT).show();
											return;
										}
									// ...ειδάλλως τον εισάγει
									ArrayAdapter adapter = (ArrayAdapter) parentView.getAdapter();
									adapter.insert(holds ? new Hold(num.doubleValue()) :  num.intValue(), other);
									adapter.notifyDataSetChanged();
									setIncomeTaxInfo(parentView);
								} catch (NumberFormatException e) {}
							})
							.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {
								parentView.setSelection(0);
								((ArrayAdapter) parentView.getAdapter()).notifyDataSetChanged();
								setIncomeTaxInfo(parentView);
							})
							.show();
				} else setIncomeTaxInfo(parentView);
			}
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		};

		// Πρόγραμμα σε κατάσταση Πολεμικής Αεροπορίας ή Ελληνικού Στρατού
		//TODO: Αφαίρεση του try-catch μετά το 2021
		try { setMode(pref.getBoolean(ARM_TYPE, false)); }
		catch(ClassCastException ex) { setMode(pref.getInt(ARM_TYPE, 0) == 1); }
		// Αρχικοποίηση spinner ΦΠΑ
		Spinner spinner = findViewById(R.id.spVAT);
		spinner.setAdapter(createAdapter(VAT_DATA));
		spinner.setOnItemSelectedListener(listener);
		int a = pref.getInt(VAT, 0);
		spinner.setSelection(a >= 0 && a < VAT_DATA.length ? a : 0);
		// Αρχικοποίηση spinner ΦΕ
		spinner = findViewById(R.id.spIncomeTax);
		spinner.setAdapter(createAdapter(INCOME_TAX_DATA));
		spinner.setOnItemSelectedListener(listener);
		a = pref.getInt(INCOME_TAX, 0);
		spinner.setSelection(a >= 0 && a < INCOME_TAX_DATA.length ? a : 0);
		setIncomeTaxInfo(spinner);
		// Αρχικοποίηση spinner κρατήσεων (αρχικοποιείται στο setMode())
		spinner = findViewById(R.id.spHolds);
		spinner.setOnItemSelectedListener(listener);
		a = pref.getInt(HOLDS, 0);
		spinner.setSelection(a < spinner.getAdapter().getCount() ? a : 0);
		// Αρχικοποίηση spinner προμηθευτή
		spinner = findViewById(R.id.spContractorType);
		a = pref.getInt(CONTRACTOR, 0);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Περιγραφή του είδους του προμηθευτή
				((TextView) findViewById(R.id.tvContractorInfo)).setText(getResources().getStringArray(R.array.contractorInfo)[position]);
				// Αν δεν είναι ιδιώτης, δε χρειάζονται components για ΦΕ
				findViewById(R.id.layIncomeTax).setVisibility(position != 0 /* Όχι ιδιώτης */ ? View.GONE : View.VISIBLE);
				// Αν είναι στρατός, δε χρειάζονται components για ΦΠΑ
				findViewById(R.id.layVAT).setVisibility(position == 2 /* Στρατός */ ? View.GONE : View.VISIBLE);
				// Αν δεν είναι ιδιώτης, δε χρειάζονται components για έργο ΜΧ
				findViewById(R.id.cbConstruction).setVisibility(position != 0 /* Όχι ιδιώτης */ || airforce ? View.GONE : View.VISIBLE);
				// Αν είναι στρατός, δε χρειάζονται components για το είδος τιμολογίου
				findViewById(R.id.layInvoiceType).setVisibility(position == 2 /* στρατός */ ? View.GONE : View.VISIBLE);
				// Επανυπολογισμός
				calculation();
			}
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		});
		spinner.setSelection(a >= 0 && a < spinner.getCount() ? a : 0);
		// Κοινός listener για τον τύπο της αξίας, του τιμολογίου, της χρηματοδότησης
		AdapterView.OnItemSelectedListener swListener = new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(final AdapterView<?> parentView, View selectedItemView, int position, long id) { calculation(); }
			@Override public void onNothingSelected(AdapterView<?> parentView) {}
		};
		// Αρχικοποίηση spinners του τύπου της αξίας
		spinner = findViewById(R.id.spAmountType);
		spinner.setOnItemSelectedListener(swListener);
		spinner.setSelection(pref.getInt(AMOUNT_TYPE, 0));
		// Αρχικοποίηση spinners του τύπου του τιμολογίου
		spinner = findViewById(R.id.spInvoiceType);
		spinner.setOnItemSelectedListener(swListener);
		spinner.setSelection(pref.getInt(INVOICE_TYPE, 0));
		// Αρχικοποίηση spinners του τύπου της χρηματοδότησης
		spinner = findViewById(R.id.spFinancingType);
		spinner.setOnItemSelectedListener(swListener);
		spinner.setSelection(pref.getInt(FINANCING_TYPE, 0));
		// Αρχικοποίηση edittext για την αξία
		EditText editText = findViewById(R.id.txtAmount);
		editText.addTextChangedListener(new TextWatcher() {
			@Override public void afterTextChanged(Editable s) { calculation(); }
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		float b = pref.getFloat(AMOUNT, 0);
		if (b > 0) editText.setText(Float.toString(b).replace(".0", ""));
		// Αρχικοποίηση compound button για τον αυτόματο υπολογισμό
		CompoundButton checkBox = findViewById(R.id.swAuto);
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				findViewById(R.id.layAdvanced).setVisibility(View.GONE);
				findViewById(R.id.layAutomatic).setVisibility(View.VISIBLE);
				// Το επεξηγηματικό κείμενο του κομβίου αυτόματου υπολογισμού
				((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(
						airforce ? R.string.tvAirForceWarning : R.string.tvAutoOn));
			} else {
				findViewById(R.id.layAdvanced).setVisibility(View.VISIBLE);
				findViewById(R.id.layAutomatic).setVisibility(View.GONE);
				((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(R.string.tvAutoOff));
			}
			calculation();
		});
		checkBox.setChecked(pref.getBoolean(AUTOMATIC, true));
		// Αρχικοποίηση checkbox για έργο ΜΧ
		checkBox = findViewById(R.id.cbConstruction);
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> calculation());
		checkBox.setChecked(pref.getBoolean(CONSTRUCTION, false));
	}

	/** Θέτει τη λειτουργία του προγράμματος για Στρατό ή Αεροπορία.
	 * Τροποποιεί το spinner των κρατήσεων, το επεξηγηματικό κείμενο του αυτόματου υπολογισμού, το
	 * αν η δαπάνη είναι έργο ή όχι, τις κατηγορίες τιμολογίου και κάνει επανυπολογισμό.
	 * @param airforce Το πρόγραμμα τίθεται σε λειτουργία Αεροπορίας */
	private void setMode(boolean airforce) {
		// Τίθεται η μεταβλητή
		this.airforce = airforce;
		// Οι συνέπειες της μεταβλητής
		Hold[] holdList = airforce ? holdListAirForce : holdListArmy;
		ArrayAdapter adapter = createAdapter(holdList);
		Spinner spinner = findViewById(R.id.spHolds);
		spinner.setAdapter(adapter);
		// Το επεξηγηματικό κείμενο του κομβίου αυτόματου υπολογισμού
		if (isAuto())
			((TextView) findViewById(R.id.tvAutoInfo)).setText(getString(
					airforce ? R.string.tvAirForceWarning : R.string.tvAutoOn));
		// Ο τίτλος του προγράμματος
		setTitle(airforce ? R.string.app_name2 : R.string.app_name);
		// Αν είναι αεροπορία, δε χρειάζονται components για έργο ΜΧ
		findViewById(R.id.cbConstruction).setVisibility(airforce ? View.GONE : View.VISIBLE);
		// Τα είδη τιμολογίου, είναι διαφορετικά στο Στρατό και στην Αεροπορία
		adapter = new ArrayAdapter(
				MainActivity.this, android.R.layout.simple_spinner_item,
				getResources().getTextArray(
						airforce ? R.array.InvoiceTypeAirForce : R.array.InvoiceTypeArmy));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) findViewById(R.id.spInvoiceType)).setAdapter(adapter);
		calculation();
	}

	/** Ο αυτόματος υπολογισμός είναι ενεργός.
	 * @return Ο αυτόματος υπολογισμός είναι ενεργός */
	private boolean isAuto() { return ((CompoundButton) findViewById(R.id.swAuto)).isChecked(); }

	/** Κοινός κώδικας που δημιουργεί τον ArrayAdapter του spinner του ΦΠΑ, κρατήσεων και ΦΕ.
	 * @param array Το array κρατήσεων, ΦΕ ή ΦΠΑ που θα περιέχει ο adapter
	 * @return Ο adapter */
	private ArrayAdapter createAdapter(Object[] array) {
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
				new ArrayList<>(Arrays.asList(array)));
		adapter.add("Άλλο");
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id) {
			case R.id.action_about:     // Menu "Σχετικά"
				new AlertDialog.Builder(this)
						.setTitle(R.string.about)
						.setMessage(String.format(getString(R.string.aboutInfo), BuildConfig.VERSION_CODE))
						.setNeutralButton(android.R.string.ok, null)
						.setCancelable(true)
						.create().show();
				return true;
			case R.id.action_arm:       // Menu "Κλάδος ΕΔ"
				new AlertDialog.Builder(this)
						.setTitle(R.string.arm)
						.setPositiveButton(R.string.armArmy, (dialog, which) -> setMode(false))
						.setNeutralButton(R.string.armAirForce, (dialog, which) -> setMode(true))
						.show();
				return true;
			default: return super.onOptionsItemSelected(item);
		}
	}

	/** Εμφανίζει πληροφορίες σε ένα TextView, σχετικές με το ποσοστό ΦΕ του τιμολογίου.
	 * Η κλήση εκτελείται όταν τροποποιούνται οι κρατήσεις, το ΦΠΑ ή το ΦΕ. Αν τροποποιείται το ΦΕ,
	 * για το νέο ΦΕ εμφανίζει πληροφορίες σχετικά με το ΦΕ. Σε κάθε περίπτωση, επανυπολογίζονται τα
	 * ποσά του τιμολογίου.
	 * @param s Το μενού στο οποίο άλλαξε η επιλογή. Είναι είτε το μενού κρατήσεων, είτε ΦΠΑ, είτε ΦΕ. */
	private void setIncomeTaxInfo(AdapterView s) {
		if (s == findViewById(R.id.spIncomeTax)) {
			String [] ar = getResources().getStringArray(R.array.IncomeTaxInfo);
			int sel = s.getSelectedItemPosition();
			String txt = sel < ar.length ? ar[sel] : "";
			((TextView) findViewById(R.id.tvFEInfo)).setText(txt);
		}
		calculation();
	}

	@Override
	public void onStop() {
		// Αποθήκευση των στοιχείων του παραθύρου
		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
		edit.putInt(CONTRACTOR, ((Spinner) findViewById(R.id.spContractorType)).getSelectedItemPosition());
		edit.putInt(AMOUNT_TYPE, ((Spinner) findViewById(R.id.spAmountType)).getSelectedItemPosition());
		edit.putInt(INVOICE_TYPE, ((Spinner) findViewById(R.id.spInvoiceType)).getSelectedItemPosition());
		edit.putBoolean(ARM_TYPE, airforce);
		edit.putBoolean(AUTOMATIC, ((Switch) findViewById(R.id.swAuto)).isChecked());
		edit.putInt(FINANCING_TYPE, ((Spinner) findViewById(R.id.spFinancingType)).getSelectedItemPosition());
		edit.putBoolean(CONSTRUCTION, ((CheckBox) findViewById(R.id.cbConstruction)).isChecked());
		edit.putInt(VAT, ((Spinner) findViewById(R.id.spVAT)).getSelectedItemPosition());
		edit.putInt(INCOME_TAX, ((Spinner) findViewById(R.id.spIncomeTax)).getSelectedItemPosition());
		edit.putInt(HOLDS, ((Spinner) findViewById(R.id.spHolds)).getSelectedItemPosition());
		String s = ((EditText) findViewById(R.id.txtAmount)).getText().toString();
		edit.putFloat(AMOUNT, s.isEmpty() ? 0 : Float.parseFloat(s));
		edit.apply();
		super.onStop();
	}

	/** Υπολογισμός της καθαρής αξίας όταν δίνεται καταλογιστέο, πληρωτέο ή υπόλοιπο πληρωτέο.
	 * @param contractor Ο τύπος του εκδότη του τιμολογίου
	 * @param amountType Ο τύπος του ποσού (καθαρή αξία, καταλογιστέο, πληρωτέο, υπόλοιπο πληρωτέο)
	 * @param amount Το ποσό
	 * @param fpa Το ποσοστό ΦΠΑ
	 * @param holds Το ποσοστό κρατήσεων
	 * @param fe Το ποσοστό ΦΕ
	 * @return Η καθαρή αξία του τιμολογίου */
	static private double calculateNet(int contractor, int amountType, double amount, double fpa,
									   double holds, double fe) {
		switch(contractor) {
			case 0:	// Ιδιώτης
				switch(amountType) {
					case 1: amount /= 1.0 + fpa; break;	// Καταλογιστέο
					case 2: amount /= 1.0 + fpa - holds; break; // Πληρωτέο
					case 3: // Υπόλοιπο πληρωτέο
						// Στις εργολαβίες το ΦΕ υπολογίζεται επί της καθαρής αξίας, ειδάλλως επί της καθαρής αξίας μειον κρατήσεις
						double feFactor = 1.0 - (fe == 0.03 ? 0 : holds);
						amount /= 1.0 + fpa - holds - fe * feFactor;
					// default: Καθαρή αξία
				}
				break;
			case 1:	// ΝΠΔΔ
				switch(amountType) {
					case 1: amount /= 1.0 + fpa + holds; break;	// Καταλογιστέο
					case 2: case 3: amount /= 1.0 + fpa; // Πληρωτέο ή Υπόλοιπο πληρωτέο
					// default: Καθαρή αξία
				}
				break;
			default:	//case 2:	// Στρατός
				if (amountType == 1) amount /= 1.0 + holds;	// Καταλογιστέο
				// else: Καθαρή αξία ή Πληρωτέο ή Υπόλοιπο πληρωτέο
		}
		// Στρογγυλοποίηση
		return Math.round(amount * 100.0) / 100.0;
	}

	/** Αυτοματοποιημένος υπολογισμός ποσοστού κρατήσεων του τιμολογίου για το Στρατό.
	 * @param contractor Ο τύπος του εκδότη του τιμολογίου
	 * @param invoiceType Ο τύπος του τιμολογίου
	 * @param financing Ο τύπος της χρηματοδότησης
	 * @param net Η καθαρή αξία του τιμολογίου
	 * @return Οι κρατήσεις του τιμολογίου */
	static private Hold calculateHoldArmy(int contractor, int invoiceType, int financing, double net) {
		Hold hold;
		if (invoiceType == 4 /*Λογαριασμοί νερού/ΔΕΗ*/) hold = holdListArmy[0];
		else if (contractor == 0 /*Ιδιώτης*/) {
			if (invoiceType == 3 /*Μισθώματα ακινήτων*/)
				switch(financing) {
					case 0: /*Τακτικός Π/Υ*/ hold = holdListArmy[2]; break;
					case 1: /*Ιδιοι πόροι*/ hold = holdListArmy[13]; break;
					default: /*case 2: Π/Υ ΠΔΕ*/ hold = holdListArmy[0];
				}
			else if (net >= PRICE_HOLD_CONTRACT)
				switch(financing) {
					case 0: /*Τακτικός Π/Υ*/
						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 6 : 5];
						break;
					case 1: /*Ιδιοι πόροι*/
						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 17 : 16];
						break;
					default: /*case 2: Π/Υ ΠΔΕ*/
						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 10 : 9];
				}
			else
				switch(financing) {
					case 0: /*Τακτικός Π/Υ*/
						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 4 : 3];
						break;
					case 1: /*Ιδιοι πόροι*/
						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 15 : 14];
						break;
					default: /*case 2: Π/Υ ΠΔΕ*/
						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 8 : 7];
				}
			// contractor: 1:ΝΠΔΔ 2:Στρατος
		} else hold = holdListArmy[financing == 1 /*Ιδιοι πόροι*/ ? 12 : 1];
		return hold;
	}

	/** Αυτοματοποιημένος υπολογισμός ποσοστού κρατήσεων του τιμολογίου για την Αεροπορία.
	 * @param invoiceType Ο τύπος του τιμολογίου
	 * @param financing Ο τύπος της χρηματοδότησης
	 * @param net Η καθαρή αξία του τιμολογίου
	 * @return Οι κρατήσεις του τιμολογίου */
	static private Hold calculateHoldAirForce(int invoiceType, int financing, double net) {
		// Ταύτιση με το invoiceType του Στρατού, προκειμένου η κλήση να μοιάζει με αυτή του Στρατου
		switch(invoiceType) {
			case 3: ++invoiceType; break;
			case 4: invoiceType = 7; break;
		}
		Hold hold;
		if (invoiceType == 4 /*Λογαριασμοί νερού/ΔΕΗ*/) hold = holdListAirForce[0];
		else if (invoiceType == 7) hold = holdListAirForce[net >= PRICE_HOLD_CONTRACT ? 4 : 3];
//		else if (contractor == 0 /*Ιδιώτης*/) {
//			if (invoiceType == 3 /*Μισθώματα ακινήτων*/)
//				switch(financing) {
//					case 0: /*Τακτικός Π/Υ*/ hold = holdListArmy[2]; break;
//					case 1: /*Ιδιοι πόροι*/ hold = holdListArmy[13]; break;
//					default: /*case 2: Π/Υ ΠΔΕ*/ hold = holdListArmy[0];
//				}
			else if (net >= PRICE_HOLD_CONTRACT)
				switch(financing) {
					case 0: /*Τακτικός Π/Υ*/
						hold = holdListAirForce[2];//invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 6 : 5];
						break;
//					case 1: /*Ιδιοι πόροι*/
//						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 17 : 16];
//						break;
					default: /*case 2: Π/Υ ΠΔΕ*/
						hold = holdListAirForce[4];//invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 10 : 9];
				}
			else
				switch(financing) {
					case 0: /*Τακτικός Π/Υ*/
						hold = holdListAirForce[1];//invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 4 : 3];
						break;
//					case 1: /*Ιδιοι πόροι*/
//						hold = holdListArmy[invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 15 : 14];
//						break;
					default: /*case 2: Π/Υ ΠΔΕ*/
						hold = holdListAirForce[3];//invoiceType >= 5 /*Εκπόνηση/Επίβλεψη μελετών*/ ? 8 : 7];
				}
			// contractor: 1:ΝΠΔΔ 2:Στρατος
//		} else hold = holdListArmy[financing == 1 /*Ιδιοι πόροι*/ ? 12 : 1];
		return hold;
	}

	/** Αυτοματοποιημένος υπολογισμός ποσοστού κρατήσεων του τιμολογίου.
	 * @param contractor Ο τύπος του εκδότη του τιμολογίου
	 * @param invoiceType Ο τύπος του τιμολογίου
	 * @param financing Ο τύπος της χρηματοδότησης
	 * @param net Η καθαρή αξία του τιμολογίου
	 * @return Οι κρατήσεις του τιμολογίου */
	private Hold calculateHold(int contractor, int invoiceType, int financing, double net) {
		return airforce ? calculateHoldAirForce(invoiceType, financing, net)
				: calculateHoldArmy(contractor, invoiceType, financing, net);
	}

	/** Η καθαρή αξία πάνω από την οποία έχουμε σύμβαση. */
	static final private int PRICE_CONTRACT = 2500;
	/** Η καθαρή αξία πάνω από την οποία οι κρατήσεις προσαυξάνονται. */
	static final private int PRICE_HOLD_CONTRACT = 1000;
	/** Η καθαρή αξία κάτω από την οποία δεν έχουμε ΦΕ. */
	static final private int PRICE_INCOME_TAX = 150;

	/** Το υπολογιστικό τμήμα του προγράμματος. */
	private void calculation() {
		try {
			// Λήψη όλων των απαραίτητων δεδομένων για τους υπολογισμούς
			int contractor = ((Spinner) findViewById(R.id.spContractorType)).getSelectedItemPosition();
			int amountType = ((Spinner) findViewById(R.id.spAmountType)).getSelectedItemPosition();
			int invoiceType = ((Spinner) findViewById(R.id.spInvoiceType)).getSelectedItemPosition();
			boolean auto = isAuto();
			CompoundButton cbConstruction = findViewById(R.id.cbConstruction);
			boolean construction = cbConstruction.isChecked();
			double amount = Double.parseDouble(((EditText) (findViewById(R.id.txtAmount))).getText().toString());
			double fpaPercent = Double.parseDouble(((Spinner) findViewById(R.id.spVAT)).getSelectedItem().toString()) / 100.0;
			double fePercent;
			Hold hold;

			// Αυτόματη εύρεση κρατήσεων - ΦΕ
			// Το πρόβλημα εδώ είναι ότι δεν ξέρουμε την καθαρή αξία!
			// Για το λόγο αυτό κάποιοι έλεγχοι θα γίνουν μεταγενέστερα.
			if (auto) {
				Spinner spFinancingType = findViewById(R.id.spFinancingType);
				int financing = spFinancingType.getSelectedItemPosition();

				// Περιορισμοί
				// Σε δαπάνες Π/Υ ΠΔΕ, προμηθευτής είναι πάντα ιδιώτης
				if (contractor != 0 /*Όχι ιδιώτης*/ && financing == 2 /*Π/Υ ΠΔΕ*/)
					spFinancingType.setSelection(financing = 0);
				// Σε κατασκευαστικές δαπάνες, προμηθευτής είναι πάντα ιδιώτης
				if (contractor != 0 /*Όχι ιδιώτης*/) construction = false;
				// Σε τιμολόγιο αεροπορίας
				if (airforce) {
					construction = false;               // δεν έχουμε κατασκευαστικές δαπάνες
					// δεν υποστηρίζεται χρηματοδότηση ιδίων πόρων
					if (financing == 1 /*Ίδιοι Πόροι*/) spFinancingType.setSelection(financing = 0);
					if (contractor != 0 /*Ιδιώτης*/)	// και προμηθευτής είναι πάντα ιδιώτης
						((Spinner) findViewById(R.id.spContractorType)).setSelection(contractor = 0);
				}
				// Σε κατασκευαστικές δαπάνες, τιμολόγια προμήθειας υλικών, παροχής υπηρεσιών ή εκπόνησης μελετών μόνο
				if (construction && invoiceType != 0 /*Προμήθεια υλικών*/ && invoiceType != 1 /*Παροχή υπηρεσιών*/
						&& invoiceType < 5 /*5:Εκπόνηση & 6:Επίβλεψη μελετών*/)
					cbConstruction.setChecked(construction = false);
				// Σε δαπάνες που προμηθευτής είναι ο Στρατός, τα τιμολόγια είναι πάντα προμήθειας υλικών
				if (contractor == 2 /*Στρατός*/ && invoiceType != 0 /*Προμήθεια υλικών*/)
					invoiceType = 0; //Προμήθεια υλικών

				// Υπολογισμός του ΦΕ
				if (contractor != 0 /*Όχι ιδιώτης*/ || invoiceType == 3 /*Μίσθωση ακινήτου για Στρατό, Λογαριασμοί νερού - έργα ΔΕΗ για Αεροπορία*/
						|| !airforce && invoiceType == 4 /*Λογαριασμοί νερού - έργα ΔΕΗ*/) fePercent = 0;
				// Μεταγενέστερα: if (!construction && net <= 150) fePercent = 0;
				else if (invoiceType == 2 /*Προμήθεια υγρών καυσίμων*/) fePercent = 0.01;
				else if (invoiceType == 1 /*Παροχή υπηρεσιών*/)
					fePercent = construction /*Κατασκευή έργου*/ ? 0.03 : 0.08;
				else if (invoiceType == 6 /*Επίβλεψη μελέτης*/) fePercent = 0.1;
				else fePercent = 0.04; /*invoiceType: 0:Προμήθεια υλικών ή 5:Εκπόνηση μελέτης ή 4:Τροφοδοσία/Καθαριότητα Οπλιτών Αεροπορίας*/

				// Υπολογισμός κρατήσεων, αρχικά θεωρώντας την καθαρή αξία >= PRICE_HOLD_CONTRACT
				// To +0.001 προστίθεται για να αποφευχθεί κάποια καταστροφική αποκοπή
				hold = calculateHold(contractor, invoiceType, financing, PRICE_HOLD_CONTRACT + 0.001);
				// Εύρεση καθαρής αξίας
				double net = calculateNet(contractor, amountType, amount, fpaPercent, hold.total, 0);
				// Αν καθαρή αξία < PRICE_HOLD_CONTRACT, επανυπολογισμός κρατήσεων και καθαρής αξίας
				if (net < PRICE_HOLD_CONTRACT) {
					hold = calculateHold(contractor, invoiceType, financing, net);
					net = calculateNet(contractor, amountType, amount, fpaPercent, hold.total, 0);
				}
				// Αν καθαρή αξία <= PRICE_INCOME_TAX και δεν έχουμε κατασκευή, το ΦΕ είναι 0
				if (!construction && net <= PRICE_INCOME_TAX) fePercent = 0;
			} else {
				// Εύρεση κρατήσεων και ΦΕ
				hold = (Hold) ((Spinner) findViewById(R.id.spHolds)).getSelectedItem();
				fePercent = Double.parseDouble(((Spinner) findViewById(R.id.spIncomeTax)).getSelectedItem().toString()) / 100.0;
			}
			// Εύρεση καθαρής αξίας
			amount = calculateNet(contractor, amountType, amount, fpaPercent, hold.total, fePercent);
			// Υπολογισμοί ενδιάμεσων τιμών
			double fpa = Math.round(amount * fpaPercent * 100.0) / 100.0;
			double holds = Math.round(amount * hold.total * 100.0) / 100.0;
			double mixed = amount;
			if (contractor != 2) mixed += fpa;		// όχι Στρατός
			if (contractor != 0) mixed += holds;	// ΝΠΔΔ, Στρατός
			double final1 = mixed - holds;
			double fe = amount - (fePercent == 0.03 ? 0 : holds);
			fe = Math.round(fe * fePercent * 100.0) / 100.0;
			double final2 = final1 - fe;
			// Εξαγωγή αποτελεσμάτων
			DecimalFormat df = new DecimalFormat("0.#####%");	// ποσοστά
			DecimalFormat df2 = new DecimalFormat("0.00¤");	// νομισματικά
			StringBuilder txt = new StringBuilder(4096);
			txt.append(String.format(getString(R.string.resNet), df2.format(amount)));
			String txtHolds = String.format(getString(R.string.resHolds),
					df.format(hold.total), df2.format(holds));
			if (contractor != 0 /* ΝΠΔΔ, Στρατός, Ύδρευση-Έργα ΔΕΗ */) txt.append("+ ").append(txtHolds);
			if (contractor != 2 /* όχι Στρατός */)
				txt.append(String.format(getString(R.string.resVAT),
						df.format(fpaPercent), df2.format(fpa)));
			txt.append(String.format(getString(R.string.resMixed),
					df2.format(mixed), txtHolds, df2.format(final1)));
			if (contractor == 0 /* Ιδιώτης */ && fePercent > 0)
				txt.append(String.format(getString(R.string.resIncomeTax),
						df.format(fePercent), df2.format(fe), df2.format(final2)));
			((TextView) findViewById(R.id.tvResults)).setText(txt);
			// Εξαγωγή απαιτούμενων
			txt = new StringBuilder(4096);
			if (contractor == 0) { // Ιδιώτης
				if (mixed > 1500) txt.append(getString(R.string.reqTaxCurrency)).append("\n");
				if (mixed > 3000) txt.append(getString(R.string.reqInsuranceCurrency)).append("\n");
				//if (amount > 2500) txt.append(getString(R.string.reqInsuranceCurrency)).append("\n");
				if (amount > PRICE_CONTRACT) txt.append(getString(R.string.reqCriminalRecord)).append("\n");
			}
			if (contractor != 2 /* όχι Στρατός */ && (amount > PRICE_CONTRACT || construction && auto))
				txt.append(getString(R.string.reqContract)).append("\n");
			if (amount > 60000) txt.append(getString(R.string.reqCompetitionFormal)).append("\n");
			else if (amount > 20000 || construction && auto) txt.append(getString(R.string.reqCompetitionInformal)).append("\n");
			if (amount > 20000) txt.append(getString(R.string.reqCriminalRecordDenial)).append("\n");
			if (auto && construction && invoiceType == 1 /* Παροχή υπηρεσιών */)
				txt.append(String.format(getString(R.string.reqConstructionContractor),
						df2.format(amount * 0.01), df2.format(amount * 0.006)))
						.append("\n");
			if (txt.length() == 0) txt.append(getString(R.string.reqEmpty)).append("\n");
			((TextView) findViewById(R.id.tvRequirements)).setText(txt.substring(0, txt.length() - 1));	// remove last newline
			// Ανάλυση κρατήσεων
			txt = new StringBuilder(4096);
			if (hold.data != null) {
				double[] holdsAll = hold.euro(holds);
				String[] txtHoldsAll = getResources().getStringArray(airforce ? R.array.hldPartsAirForce : R.array.hldPartsArmy);
				String str = getString(R.string.hldPart);
				txt.append(String.format(str, getString(R.string.hldTotal), df.format(hold.total),
						df2.format(holds)));
				for (int z = 0; z < holdsAll.length; ++z)
					if (z >= hold.data.length) break;
					else if (hold.data[z] != 0)
						txt.append(String.format(str, txtHoldsAll[z], df.format(hold.data[z]),
								df2.format(holdsAll[z])));
			} else txt.append(getString(R.string.hldFail));
			((TextView) findViewById(R.id.tvHoldAnalysis)).setText(txt);
			// Εμφάνιση των widgets που είναι κρυφά όσο δεν υπάρχει αποτέλεσμα
			findViewById(R.id.layOut).setVisibility(View.VISIBLE);
		} catch (NumberFormatException e) {
			findViewById(R.id.layOut).setVisibility(View.GONE);
		}
	}

	/** Κράτηση. */
	static private class Hold {
		/** Δημιουργία μιας κράτησης, σαν σύνολο, χωρίς επιμέρους κρατήσεις.
		 * @param sum Το σύνολο της κράτησης */
		Hold(double sum) { data = null; total = sum / 100; }
		/** Δημιουργία μιας κράτησης, με τις επιμέρους κρατήσεις.
		 * @param holds Οι επιμέρους κρατήσεις, χωρίς το σύνολο (που υπολογίζεται), με τη σειρά που
		 * εμφανίζονται τα ονόματά τους στο string array */
		Hold(double[] holds) {
			data = holds;
			double sum = 0;
			for (double item : data) sum += item;
			total = sum;
		}
		@Override public String toString() { return Double.toString(Math.round(total * 10000000.0)/ 100000.0).replaceAll("\\.0$", ""); }
		@Override public boolean equals(Object o) {
			if (o instanceof Hold) return ((Hold) o).total == total;
			return o instanceof Number && ((Number) o).doubleValue() == total;
		}
		/** Οι επιμέρους κρατήσεις σε €.
		 * @param holds Το συνολικό ποσό των κρατήσεων σε €
		 * @return Οι επιμέρους κρατήσεις σε €, διορθωμένες ώστε να έχουν άθροισμα το συνολικό ποσό */
		double[] euro(double holds) {
			if (data == null) return null;
			double euroTotal = 0;
			double[] euroData = new double[data.length];
			class Pair {
				private Pair(double remainder, int id) { this.remainder = remainder; this.id = id; }
				private double remainder;
				private int id;
			}
			ArrayList<Pair> remainders = new ArrayList<>();
			for (int z = 0; z < data.length; ++z) {
				double t = holds * data[z] / total;
				remainders.add(new Pair(Math.round(t) - t, z));
				euroTotal += euroData[z] = Math.round(t * 100.0) / 100.0;
			}

			// Σφάλματα στρογγυλοποίησης
			int remainder = (int) Math.round((euroTotal - holds) * 100.0);
			if (remainder != 0) {
				Collections.sort(remainders, (a, b) -> Double.compare(a.remainder, b.remainder));
				if (remainder > 0)
					for (int z = 0; z < remainder; ++z) {
						Pair p = remainders.get(remainders.size() - 1 - z);
						euroData[p.id] -= 0.01;
					}
				else
					for (int z = 0; z < -remainder; ++z) {
						Pair p = remainders.get(z);
						euroData[p.id] += 0.01;
					}
			}
			return euroData;
		}
		/** Το συνολικό ποσοστό των κρατήσεων. */
		final double total;
		/** Τα ποσοστά των επιμέρους κρατήσεων. Το άθροισμά τους είναι ίσο με το συνολικό ποσοστό. */
		final private double[] data;
	}
}
