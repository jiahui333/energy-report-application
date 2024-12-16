export type HourlyReport = {
    hour: string;
    kwhUsed: number;
    cost: number;
}

export type Report = {
    meterId: string;
    totalEnergy: number;
    totalCost: number;
    hourlyReports: HourlyReport[];
}