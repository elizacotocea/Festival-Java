namespace java transformer
namespace netstd transformer

struct EmployeeDTO {
  1: i32 id,
  2: string username,
  3: string password,
}

struct ShowDTO {
  1: i32 id,
  2: string dataTimp,
  3: string location,
  4: i32 nrAvailableSeats,
  5: i32 nrSoldSeats,
  6: string artistName,
}

struct TicketDTO {
    1: i32 id,
    2: i32 nrWantedSeats,
    3: string buyerName,
    4: i32 idShow,
}

service AppService
{
	string login(1:string username, 2:string password, 3:string host, 4:i32 port),
	list<ShowDTO> findAllShows(),
	void ticketsSold(1:ShowDTO showDTO, 2:TicketDTO ticketDTO),
	string logOut(1:string username,  2:string host, 3:i32 port),
}