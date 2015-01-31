public class Course
	{
		String name;
		String number;
		String section;
		private int quota;
		
		public Course(String name, String number, String section)
		{
			this.name = name;
			this.number = number;
			this.section = section;
			quota = 0;
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

		public String getSection() {
			return section;
		}

		public void setSection(String section) {
			this.section = section;
		}
		
		public void setQuota(int quota)
		{
			this.quota = quota;
		}
		
		public int getQuota()
		{
			return quota;
		}
		
		
		
	}